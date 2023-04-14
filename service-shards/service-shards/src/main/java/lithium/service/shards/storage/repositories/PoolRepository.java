package lithium.service.shards.storage.repositories;

import lithium.service.shards.storage.entities.Pool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface PoolRepository extends JpaRepository<Pool, Long> {
	Pool findByName(String name);

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Pool findForUpdate(@Param("id") Long id);
}
