package lithium.service.report.games.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.ReportRunResults;

public interface ReportRunResultsRepository extends PagingAndSortingRepository<ReportRunResults, Long>, JpaSpecificationExecutor<ReportRunResults> {

	Page<ReportRunResults> findByReportRunId(Long reportRunId, Pageable pageRequest);

}
