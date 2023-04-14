package lithium.service.report.games.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.ReportAction;
import lithium.service.report.games.data.entities.ReportRevision;

public interface ReportActionRepository extends PagingAndSortingRepository<ReportAction, Long>, JpaSpecificationExecutor<ReportAction> {
	List<ReportAction> findByReportRevision(ReportRevision rev);
}