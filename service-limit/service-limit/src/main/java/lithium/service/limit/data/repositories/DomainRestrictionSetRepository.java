package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DomainRestrictionSetRepository extends PagingAndSortingRepository<DomainRestrictionSet, Long>, JpaSpecificationExecutor<DomainRestrictionSet> {
	DomainRestrictionSet findByDomainNameAndName(String domainName, String name);
	DomainRestrictionSet findByDomainAndName(Domain domain, String name);
	List<DomainRestrictionSet> findByDomainNameAndEnabledTrue(String domainName);
	List<DomainRestrictionSet> findByDomainNameAndEnabledTrueAndDwhVisibleTrue(String domainName);
	List<DomainRestrictionSet> findAllByIdIn(List<Long> ids);
	default DomainRestrictionSet findOne(Long id) {
		return findById(id).orElse(null);
	}
}
