package lithium.service.leader.storage.repositories;

import lithium.service.leader.storage.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface ModuleRepository extends JpaRepository<Module, Long> {
	Module findByName(String name);

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Module findForUpdate(@Param("id") Long id);
}
