package lithium.service.affiliate.data.repositories;

import java.util.List;

import lithium.service.affiliate.data.entities.ReportAction;
import lithium.service.affiliate.data.entities.ReportRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportActionRepository extends PagingAndSortingRepository<ReportAction, Long>, JpaSpecificationExecutor<ReportAction> {
	List<ReportAction> findByReportRevision(ReportRevision rev);
}
