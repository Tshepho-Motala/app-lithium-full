package lithium.service.leader.storage.repositories;

import lithium.service.leader.storage.entities.Instance;
import lithium.service.leader.storage.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
	List<Instance> findByModule(Module module);

	@Transactional
	@Modifying
	void deleteByModuleAndInstanceId(Module module, String instanceId);

	@Transactional
	@Modifying
	void deleteByLastHeartbeatBefore(Date killThreshold);
}
