package lithium.service.access.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.ExternalListRuleStatusOptionConfig;

public interface ExternalListRuleStatusOptionConfigRepository extends PagingAndSortingRepository<ExternalListRuleStatusOptionConfig, Long> {
	void deleteByExternalList(ExternalList externalList);
}
