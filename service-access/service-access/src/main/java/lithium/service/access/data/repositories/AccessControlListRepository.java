package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessRule;

public interface AccessControlListRepository extends PagingAndSortingRepository<AccessControlList, Long> {
	AccessControlList findByAccessRuleAndList(AccessRule accessRule, List list);
	java.util.List<AccessControlList> findAllByIpResetTimeNotNull();
}
