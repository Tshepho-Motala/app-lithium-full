package lithium.service.affiliate.data.repositories;

import lithium.service.affiliate.data.entities.ReportRunResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportRunResultsRepository extends PagingAndSortingRepository<ReportRunResults, Long>, JpaSpecificationExecutor<ReportRunResults> {

	Page<ReportRunResults> findByReportRunId(Long reportRunId, Pageable pageRequest);

}
