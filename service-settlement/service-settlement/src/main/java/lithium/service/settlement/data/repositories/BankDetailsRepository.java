package lithium.service.settlement.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.BankDetails;

public interface BankDetailsRepository extends PagingAndSortingRepository<BankDetails, Long>, JpaSpecificationExecutor<BankDetails> {
}
