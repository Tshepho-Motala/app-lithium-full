package lithium.service.report.players.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.ReportRun;

public interface ReportRunRepository extends PagingAndSortingRepository<ReportRun, Long>, JpaSpecificationExecutor<ReportRun> {

	Page<ReportRun> findByReportId(Long reportId, Pageable pageRequest);

	default ReportRun findOne(Long id) {
		return findById(id).orElse(null);
	}
}
