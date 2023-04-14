package lithium.service.report.games.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.ReportFilter;
import lithium.service.report.games.data.entities.ReportRevision;

public interface ReportFilterRepository extends PagingAndSortingRepository<ReportFilter, Long>, JpaSpecificationExecutor<ReportFilter> {
	List<ReportFilter> findByReportRevision(ReportRevision rev);
}