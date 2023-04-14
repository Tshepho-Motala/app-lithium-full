package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementDebit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettlementCreditRepository extends PagingAndSortingRepository<SettlementCredit, Long> {
	SettlementCredit findByBetId(Long betId);
	SettlementCredit findByAccountingTransactionId(Long accountingTransactionId);
	SettlementCredit findByRequestId(Long requestId);
}
