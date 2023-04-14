package lithium.service.cashier.mock.flutterwave.data.repositories;

import lithium.service.cashier.mock.flutterwave.data.entities.FlutterwaveTransaction;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface FlutterwaveTransactionRepository extends PagingAndSortingRepository<FlutterwaveTransaction, Long> {
    List<FlutterwaveTransaction> findAllByStatusAndFinalizedFalse(String status);

    List<FlutterwaveTransaction> findAllByStatusNotAndFinalizedFalse(String status);

    default FlutterwaveTransaction findOne(Long id){
        return Optional.ofNullable(id)
                .flatMap(this::findById)
                .orElse(null);
    }

}

