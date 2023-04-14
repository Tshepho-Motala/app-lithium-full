package lithium.service.shards.storage.repositories;

import lithium.service.shards.storage.entities.Module;
import lithium.service.shards.storage.entities.Pool;
import lithium.service.shards.storage.entities.Shard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ShardRepository extends JpaRepository<Shard, Long> {
	@Transactional
	@Modifying
	void deleteByLastHeartbeatBefore(Date evictThreshold);

	Shard findFirstByModuleAndPoolAndShutdownTrueOrLastHeartbeatBefore(Module module, Pool pool, Date staleThreshold);

	Shard findByModuleNameAndPoolNameAndUuid(String moduleName, String poolName, String uuid);

	List<Shard> findByModuleAndPoolAndUuidIn(Module module, Pool pool, List<String> uuids);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
		   "SET o.lastHeartbeat = :lastHeartbeat " +
		   "WHERE o.module = :module " +
		   "AND o.pool = :pool " +
		   "AND o.uuid in (:uuids)")
	void bulkHeartbeat(@Param("lastHeartbeat") Date lastHeartbeat, @Param("module") Module module,
			@Param("pool") Pool pool, @Param("uuids") List<String> uuids);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
		   "SET o.lastHeartbeat = :lastHeartbeat " +
		   "WHERE o.uuid in (:uuids)")
	void bulkHeartbeat(@Param("lastHeartbeat") Date lastHeartbeat, @Param("uuids") List<String> uuids);

	@Transactional
	@Modifying
	@Query("UPDATE #{#entityName} o " +
			"SET o.shutdown = true " +
			"WHERE o.uuid in (:uuids)")
	void shutdown(@Param("uuids") List<String> uuids);
}
