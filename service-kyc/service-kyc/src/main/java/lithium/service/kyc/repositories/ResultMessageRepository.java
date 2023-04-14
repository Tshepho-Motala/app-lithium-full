package lithium.service.kyc.repositories;

import lithium.service.kyc.entities.ResultMessage;
import lithium.service.kyc.entities.VerificationResult;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ResultMessageRepository extends PagingAndSortingRepository<ResultMessage, Long> {
    default ResultMessage findOne(Long id) {
        return findById(id).orElse(null);
    }
}
