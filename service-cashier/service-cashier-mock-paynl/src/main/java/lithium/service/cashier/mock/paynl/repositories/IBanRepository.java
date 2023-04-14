package lithium.service.cashier.mock.paynl.repositories;

import lithium.service.cashier.mock.paynl.data.entities.IBan;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface IBanRepository extends PagingAndSortingRepository<IBan, Long> {
    Optional<IBan> findByNumber(String number);
}
