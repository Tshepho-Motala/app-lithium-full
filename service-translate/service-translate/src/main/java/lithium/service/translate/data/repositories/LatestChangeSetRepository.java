package lithium.service.translate.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.translate.data.entities.LatestChangeSet;

public interface LatestChangeSetRepository extends PagingAndSortingRepository<LatestChangeSet, Long>, JpaSpecificationExecutor<LatestChangeSet> {
	LatestChangeSet findByNameAndLanguage_Locale2(String name, String locale3);
}
