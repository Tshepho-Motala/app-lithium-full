package lithium.service.report.players.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.ReportRevision;

public interface ReportRevisionRepository extends PagingAndSortingRepository<ReportRevision, Long>, JpaSpecificationExecutor<ReportRevision> {
		
}
