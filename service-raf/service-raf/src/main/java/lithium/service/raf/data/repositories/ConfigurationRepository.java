package lithium.service.raf.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.raf.data.entities.Configuration;

public interface ConfigurationRepository extends PagingAndSortingRepository<Configuration, Long>, JpaSpecificationExecutor<Configuration> {
	Configuration findByDomainName(String domainName);

	default Configuration findOne(Long id) {
		return findById(id).orElse(null);
	}
}
