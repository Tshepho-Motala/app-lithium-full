package lithium.service.machine.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Location;
import lithium.service.machine.data.entities.Machine;

public interface LocationRepository extends PagingAndSortingRepository<Location, Long>, JpaSpecificationExecutor<Location> {
	Page<Location> findByMachineAndDistributionConfigurationCurrentEndIsNotNull(Machine machine, Pageable pageable);
}
