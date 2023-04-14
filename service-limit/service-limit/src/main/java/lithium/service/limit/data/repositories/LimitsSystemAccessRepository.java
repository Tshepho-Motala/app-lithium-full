package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.LimitSystemAccess;
import lithium.service.limit.data.entities.VerificationStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LimitsSystemAccessRepository extends PagingAndSortingRepository<LimitSystemAccess, Long> {
    Iterable<LimitSystemAccess> findAllByDomainName(String domainName);
    LimitSystemAccess findByDomainNameAndVerificationStatus(String domainName, VerificationStatus verificationStatusId);
}
