package lithium.service.shards.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.shards.config.Properties;
import lithium.service.shards.exceptions.Status404ShardNotFoundException;
import lithium.service.shards.storage.entities.Module;
import lithium.service.shards.storage.entities.Pool;
import lithium.service.shards.storage.entities.Shard;
import lithium.service.shards.storage.repositories.ShardRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShardService {
	@Autowired private ShardService self;
	@Autowired private ModelMapper modelMapper;
	@Autowired private ModuleService moduleService;
	@Autowired private PoolService poolService;
	@Autowired private Properties properties;
	@Autowired private ShardRepository repository;

	// Persist shared data before the start of the transaction
	@TimeThisMethod
	public lithium.service.shards.objects.Shard get(String module, String pool) {
		SW.start("module.find-or-create");
		Module m = moduleService.findOrCreate(module);
		SW.stop();
		SW.start("pool.find-or-create");
		Pool p = poolService.findOrCreate(pool);
		SW.stop();
		return self.get(m, p);
	}

	@Transactional(rollbackFor = Exception.class)
	@TimeThisMethod
	public lithium.service.shards.objects.Shard get(Module module, Pool pool) {
		Date staleThreshold = DateTime.now().minusMillis(properties.getKeepAliveMs()).toDate();
		log.trace("Find or create shard | module: {}, pool: {}, lastHeartbeatBefore: {}", module.getName(),
				pool.getName(), staleThreshold);
		// Get an exclusive lock on pool
		poolService.findForUpdate(pool.getId());
		SW.start("find");
		Shard s = repository.findFirstByModuleAndPoolAndShutdownTrueOrLastHeartbeatBefore(module, pool, staleThreshold);
		SW.stop();
		if (s != null) {
			log.trace("Found an abandoned shard in pool | shard: {}", s);
			s.setShutdown(false);
			s.setLastHeartbeat(new Date());
			s = repository.save(s);
		} else {
			log.trace("Did not find any abandoned shards in pool, creating a new shard");
			SW.start("create");
			s = repository.save(Shard.builder()
					.module(module)
					.pool(pool)
					.uuid(UUID.randomUUID().toString())
					.build());
			SW.stop();
			log.trace("Created shard: {}", s);
		}
		return modelMapper.map(s, lithium.service.shards.objects.Shard.class);
	}

	@TimeThisMethod
	public void shutdown(String module, Map<String, Map<String, lithium.service.shards.objects.Shard>> pools) {
		log.trace("Shutdown | {}, pools: {} ", module, pools);
		SW.start("shutdown.uuids");
		List<String> uuids = poolsToUuidsList(pools);
		SW.stop();
		SW.start("shutdown.update");
		repository.shutdown(uuids);
		SW.stop();
	}

	@TimeThisMethod
	public Map<String, Map<String, lithium.service.shards.objects.Shard>> bulkHeartbeat(String module,
			Map<String, Map<String, lithium.service.shards.objects.Shard>> pools) {
		log.trace("Received bulk heartbeat | module: {}, pools: {}", module, pools);
		SW.start("bulkHeartbeat.uuids");
		List<String> uuids = poolsToUuidsList(pools);
		log.trace("uuids: {}", uuids);
		SW.stop();
		SW.start("bulkHeartbeat.update");
		repository.bulkHeartbeat(new Date(), uuids);
		SW.stop();
		// TODO: Get latest data
		return pools;
	}

	@TimeThisMethod
	public List<lithium.service.shards.objects.Shard> bulkHeartbeat(String module, String pool, List<String> uuids) {
		log.trace("Received bulk heartbeat | module: {}, pool: {}, uuids: {}", module, pool,
				uuids.stream().collect(Collectors.joining(",")));
		SW.start("bulkHearbeat.update");
		Module m = moduleService.findOrCreate(module);
		Pool p = poolService.findOrCreate(pool);
		repository.bulkHeartbeat(new Date(), m, p, uuids);
		SW.stop();
		SW.start("bulkHeartbeat.find");
		List<Shard> shards = repository.findByModuleAndPoolAndUuidIn(m, p, uuids);
		SW.stop();
		return shards.stream()
				.map(shard -> modelMapper.map(shard, lithium.service.shards.objects.Shard.class))
				.collect(Collectors.toList());
	}

	@TimeThisMethod
	public lithium.service.shards.objects.Shard heartbeat(String module, String pool, String uuid)
			throws Status404ShardNotFoundException {
		log.trace("Received heartbeat | module: {}, pool: {}, uuid: {}", module, pool, uuid);
		SW.start("heartbeat.find");
		Shard shard = repository.findByModuleNameAndPoolNameAndUuid(module, pool, uuid);
		SW.stop();
		if (shard == null) {
			String msg = "Shard not found | module: " + module + " , pool: " + pool + " , uuid: " + uuid;
			log.error(msg);
			throw new Status404ShardNotFoundException(msg);
		}
		shard.setLastHeartbeat(new Date());
		SW.start("heartbeat.save");
		shard = repository.save(shard);
		SW.stop();
		log.trace("Updated last heartbeat | shard: {}", shard);
		return modelMapper.map(shard, lithium.service.shards.objects.Shard.class);
	}

	@TimeThisMethod
	public void evictStaleShards() {
		Date evictThreshold = DateTime.now().minusMillis(properties.getCleanupMs()).toDate();
		log.trace("Evicting stale shards | evictThreshold: {}", evictThreshold);
		SW.start("evictStaleShards.delete");
		repository.deleteByLastHeartbeatBefore(evictThreshold);
		SW.stop();
	}

	private List<String> poolsToUuidsList(Map<String, Map<String, lithium.service.shards.objects.Shard>> pools) {
		List<String> uuids = new ArrayList<>();
		pools.entrySet().forEach(pool -> {
			pool.getValue().forEach((k, v) -> {
				uuids.add(v.getUuid());
			});
		});
		return uuids;
	}
}
