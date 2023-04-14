package lithium.service.cashier.mock.hexopay.data.repositories;

import lithium.service.cashier.mock.hexopay.data.entities.TransactionToken;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransactionTokenRepository extends PagingAndSortingRepository<TransactionToken, Long> {
    TransactionToken findByToken(String token);
    List<TransactionToken> findByTtlNotAndStatus(Long ttl, Status status);
}
