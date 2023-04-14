package lithium.service.machine.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.machine.data.entities.RelationshipDistributionConfiguration;

public interface RelationshipDistributionConfigurationRepository extends PagingAndSortingRepository<RelationshipDistributionConfiguration, Long>, JpaSpecificationExecutor<RelationshipDistributionConfiguration> {
}
