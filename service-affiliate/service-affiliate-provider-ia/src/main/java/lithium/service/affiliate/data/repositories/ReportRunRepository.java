package lithium.service.affiliate.data.repositories;

import lithium.service.affiliate.data.entities.ReportRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReportRunRepository extends PagingAndSortingRepository<ReportRun, Long>, JpaSpecificationExecutor<ReportRun> {

	Page<ReportRun> findByReportId(Long reportId, Pageable pageRequest);

	default ReportRun findOne(Long id) {
		return findById(id).orElse(null);
	}

}
