package lithium.service.leader.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.leader.config.Properties;
import lithium.service.leader.storage.entities.Instance;
import lithium.service.leader.storage.entities.Module;
import lithium.service.leader.storage.repositories.InstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LeaderService {
	@Autowired private LeaderService self;
	@Autowired private InstanceRepository instanceRepository;
	@Autowired private ModelMapper modelMapper;
	@Autowired private ModuleService moduleService;
	@Autowired private Properties properties;

	// Persist shared data before the start of the transaction
	@TimeThisMethod
	public lithium.service.leader.objects.Instance heartbeat(String moduleName, String instanceId) {
		log.trace("Received heartbeat | module: {}, instanceId: {}", moduleName, instanceId);

		SW.start("module.find");
		Module module = moduleService.findOrCreate(moduleName);
		SW.stop();

		return self.heartbeat(module, instanceId);
	}

	@Transactional(rollbackFor = Exception.class)
	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 50, random = true))
	@TimeThisMethod
	public lithium.service.leader.objects.Instance heartbeat(Module module, String instanceId) {
		// Get an exclusive lock on module
		moduleService.findForUpdate(module.getId());
		SW.start("instances.find");
		List<Instance> instances = instanceRepository.findByModule(module);
		log.trace("Instances for module {} | {}", module.getName(), instances);
		SW.stop();

		Instance instance = instances.stream()
				.filter(i -> { return i.getInstanceId().contentEquals(instanceId); })
				.findFirst()
				.orElse(null);

		if (instance != null) {
			log.trace("Updating last heartbeat for module instance with instanceId | module: {}, intanceId: {}",
					module.getName(), instanceId);
			instance.setLastHeartbeat(new Date());
			boolean hasLeader = instances.stream().anyMatch(i -> (i.getLeader() != null && i.getLeader() == true));
			if (!hasLeader) {
				log.trace("Module {} has no leader. Electing {} as the leader", module.getName(),
						instanceId);
				instance.setLeader(true);
			}
		} else {
			log.trace("Module instance does not exist, registering it | module: {}, instanceId: {}", module.getName(),
					instanceId);
			instance = Instance.builder()
					.module(module)
					.instanceId(instanceId)
					.leader((instances.isEmpty()) ? true : null)
					.build();
		}

		SW.start("instance.update");
		instance = instanceRepository.save(instance);
		log.trace("Updated instance: {}", instance);
		SW.stop();

		lithium.service.leader.objects.Instance oInstance = modelMapper.map(instance,
				lithium.service.leader.objects.Instance.class);
		oInstance.setLeader(oInstance.getLeader() != null);

		return oInstance;
	}

	@TimeThisMethod
	public void shutdown(String moduleName, String instanceId) {
		log.trace("Received shutdown signal | module: {}, instanceId: {}", moduleName, instanceId);
		SW.start("module.find");
		Module module = moduleService.findOrCreate(moduleName);
		SW.stop();
		SW.start("instance.delete");
		instanceRepository.deleteByModuleAndInstanceId(module, instanceId);
		SW.stop();
	}

	@TimeThisMethod
	public void cleanup() {
		Date killThreshold = DateTime.now().minusMillis(properties.getInstance().getKeepAliveMs()).toDate();
		log.trace("Cleaning up dead module instances | killThreshold: {}", killThreshold);
		SW.start("instances.delete");
		instanceRepository.deleteByLastHeartbeatBefore(killThreshold);
		SW.stop();
	}
}
