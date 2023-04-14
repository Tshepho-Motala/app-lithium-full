package lithium.service.kyc.repositories;

import lithium.service.kyc.entities.VerificationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface VerificationResultRepository extends PagingAndSortingRepository<VerificationResult, Long> {

    Page<VerificationResult> findAll(Specification<VerificationResult> table, Pageable pageRequest);

    Long countAllByUserGuidAndProviderGuidAndMethodTypeName(String userGuid, String provider, String methodType);

    default VerificationResult findOne(Long id) {
        return findById(id).orElse(null);
    }
}
