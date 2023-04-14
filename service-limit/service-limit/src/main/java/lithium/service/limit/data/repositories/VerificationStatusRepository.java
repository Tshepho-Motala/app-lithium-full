package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.VerificationStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VerificationStatusRepository extends PagingAndSortingRepository<VerificationStatus, Long> {

    default VerificationStatus findOne(Long id) {
        return findById(id).orElse(null);
    }

}
