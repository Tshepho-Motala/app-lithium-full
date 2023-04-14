package lithium.service.domain.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.BankingDetails;

public interface BankingDetailsRepository extends PagingAndSortingRepository<BankingDetails, Long>, JpaSpecificationExecutor<BankingDetails> {
}
