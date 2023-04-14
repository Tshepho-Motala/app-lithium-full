package lithium.service.report.players.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.ReportFilter;
import lithium.service.report.players.data.entities.ReportRevision;

public interface ReportFilterRepository extends PagingAndSortingRepository<ReportFilter, Long>, JpaSpecificationExecutor<ReportFilter> {
	List<ReportFilter> findByReportRevision(ReportRevision rev);
}