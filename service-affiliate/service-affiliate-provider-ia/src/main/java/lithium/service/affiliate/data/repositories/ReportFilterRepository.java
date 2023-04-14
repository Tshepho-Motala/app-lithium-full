package lithium.service.affiliate.data.repositories;

import java.util.List;

import lithium.service.affiliate.data.entities.ReportFilter;
import lithium.service.affiliate.data.entities.ReportRevision;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportFilterRepository extends PagingAndSortingRepository<ReportFilter, Long>, JpaSpecificationExecutor<ReportFilter> {
	List<ReportFilter> findByReportRevision(ReportRevision rev);
}
