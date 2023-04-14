package lithium.service.machine.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Relationship;

public interface RelationshipRepository extends PagingAndSortingRepository<Relationship, Long>, JpaSpecificationExecutor<Relationship> {
	Page<Relationship> findByMachineAndDistributionConfigurationCurrentEndIsNotNull(Machine machine, Pageable pageable);
}
