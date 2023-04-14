package lithium.service.affiliate.data.repositories;

import lithium.service.affiliate.data.entities.ReportRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportRevisionRepository extends PagingAndSortingRepository<ReportRevision, Long>, JpaSpecificationExecutor<ReportRevision> {
		
}
