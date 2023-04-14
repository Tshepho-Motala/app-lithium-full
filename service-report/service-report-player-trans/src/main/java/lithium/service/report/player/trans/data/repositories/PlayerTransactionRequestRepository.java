package lithium.service.report.player.trans.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.player.trans.data.entities.PlayerTransactionRequest;

public interface PlayerTransactionRequestRepository extends PagingAndSortingRepository<PlayerTransactionRequest, Long> {

}
