package lithium.service.report.player.trans.data.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.player.trans.data.entities.PlayerTransaction;
import lithium.service.report.player.trans.data.entities.PlayerTransactionQueryCriteria;

public interface PlayerTransactionRepository extends PagingAndSortingRepository<PlayerTransaction, Long>, JpaSpecificationExecutor<PlayerTransaction> {

	Page<PlayerTransaction> findByUserGuidAndTranEntryDateIsBetweenAndQueryCriteriaId(String userGuid, Date startDate, Date endDate, Long queryCriteriaId, Pageable pageable);
}
