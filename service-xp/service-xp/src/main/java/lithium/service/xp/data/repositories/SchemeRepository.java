package lithium.service.xp.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.data.entities.Status;

public interface SchemeRepository extends PagingAndSortingRepository<Scheme, Long>, JpaSpecificationExecutor<Scheme> {
	Scheme findByDomainNameAndStatus(String domainName, Status status);
	Scheme findByDomainNameAndNameIgnoreCase(String domainName, String name);

	default Scheme findOne(Long id) {
		return findById(id).orElse(null);
	}

}
