package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementCredit;
import lithium.service.casino.provider.sportsbook.storage.entities.SettlementDebit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettlementDebitRepository extends PagingAndSortingRepository<SettlementDebit, Long> {
	SettlementDebit findByAccountingTransactionId(Long accountingTransactionId);
	SettlementDebit findByRequestId(Long requestId);
}
