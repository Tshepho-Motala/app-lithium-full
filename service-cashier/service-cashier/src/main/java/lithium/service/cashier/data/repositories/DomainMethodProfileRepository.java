package lithium.service.cashier.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProfile;
import lithium.service.cashier.data.entities.Profile;

public interface DomainMethodProfileRepository extends PagingAndSortingRepository<DomainMethodProfile, Long>, JpaSpecificationExecutor<DomainMethodProfile> {
	DomainMethodProfile findByDomainMethodAndProfile(DomainMethod domainMethod, Profile profile);
	List<DomainMethodProfile> findByProfile(Profile profile);

	default DomainMethodProfile findOne(Long id) {
		return findById(id).orElse(null);
	}
}