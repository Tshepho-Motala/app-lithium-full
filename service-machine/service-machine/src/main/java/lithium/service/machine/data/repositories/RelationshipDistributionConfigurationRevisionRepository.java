package lithium.service.machine.data.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.RelationshipDistributionConfiguration;
import lithium.service.machine.data.entities.RelationshipDistributionConfigurationRevision;

public interface RelationshipDistributionConfigurationRevisionRepository extends PagingAndSortingRepository<RelationshipDistributionConfigurationRevision, Long>, JpaSpecificationExecutor<RelationshipDistributionConfigurationRevision> {
	List<RelationshipDistributionConfigurationRevision> findByRelationshipDistributionConfigurationRelationshipMachine(Machine machine);
	Page<RelationshipDistributionConfigurationRevision> findByRelationshipDistributionConfigurationRelationshipMachineOrderByStartDescEndDesc(Machine machine, Pageable pageable);
	List<RelationshipDistributionConfigurationRevision> findByRelationshipDistributionConfiguration(RelationshipDistributionConfiguration config);
	@Modifying
	@Transactional
	void deleteByRelationshipDistributionConfiguration(RelationshipDistributionConfiguration relationshipDistConfig);
	Page<RelationshipDistributionConfigurationRevision> findByRelationshipDistributionConfigurationAndEndNotNullOrderByEndDesc(RelationshipDistributionConfiguration config, Pageable pageable);
}
