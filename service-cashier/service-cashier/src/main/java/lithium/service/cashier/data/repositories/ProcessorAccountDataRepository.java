package lithium.service.cashier.data.repositories;

import lithium.service.cashier.data.entities.ProcessorAccountData;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProcessorAccountDataRepository extends PagingAndSortingRepository<ProcessorAccountData, Long> {
    ProcessorAccountData findByProcessorAccount(ProcessorUserCard paymentMethod);
}
