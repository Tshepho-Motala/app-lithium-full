package lithium.service.report.player.trans.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.player.trans.data.entities.PlayerTransactionQueryCriteria;

public interface PlayerTransactionQueryCriteriaRepository extends PagingAndSortingRepository<PlayerTransactionQueryCriteria, Long> {
	public PlayerTransactionQueryCriteria findByHash(String hash);
}
