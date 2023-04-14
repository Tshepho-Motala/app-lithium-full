package lithium.service.access.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.AccessRule;

public interface AccessRuleRepository extends PagingAndSortingRepository<AccessRule, Long>, JpaSpecificationExecutor<AccessRule> {
	AccessRule findByDomainNameAndNameIgnoreCase(String domainName, String name);
	List<AccessRule> findByDomainNameAndEnabledTrue(String domainName);
	List<AccessRule> findByDomainName(String domainName);
  default AccessRule findOne(Long id) {
    return findById(id).orElse(null);
  }
}
