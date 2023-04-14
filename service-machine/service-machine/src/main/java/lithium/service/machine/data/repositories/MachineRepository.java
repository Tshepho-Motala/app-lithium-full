package lithium.service.machine.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Status;

public interface MachineRepository extends PagingAndSortingRepository<Machine, Long>, JpaSpecificationExecutor<Machine> {
	Machine findByDomainAndGuid(Domain domain, String guid);
	
	Integer countByDomainAndStatus(Domain domain, Status status);
	Integer countByDomainAndStatusAndLastPingGreaterThan(Domain domain, Status status, Date lastPing);
	
	List<Machine> findByIdGreaterThanOrderById(Long machineId);
	Page<Machine> findByIdGreaterThanOrderById(Long machineId, Pageable pageable);

	default Machine findOne(Long id) {
		return findById(id).orElse(null);
	}
}