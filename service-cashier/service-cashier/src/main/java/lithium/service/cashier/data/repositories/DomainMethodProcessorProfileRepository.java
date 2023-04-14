package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProfile;
import lithium.service.cashier.data.entities.Profile;

public interface DomainMethodProcessorProfileRepository extends PagingAndSortingRepository<DomainMethodProcessorProfile, Long> {
	DomainMethodProcessorProfile findByDomainMethodProcessorAndProfile(DomainMethodProcessor domainMethodProcessor, Profile profile);
	List<DomainMethodProcessorProfile> findByProfile(Profile profile);
	List<DomainMethodProcessorProfile> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor);
	Page<DomainMethodProcessorProfile> findByDomainMethodProcessor(DomainMethodProcessor domainMethodProcessor, Pageable pageable);
	
//	List<DomainMethodProcessorProfile> findByDomainMethodProfile(DomainMethodProfile domainMethodProfile);

	default DomainMethodProcessorProfile findOne(Long id) {
		return findById(id).orElse(null);
	}
}