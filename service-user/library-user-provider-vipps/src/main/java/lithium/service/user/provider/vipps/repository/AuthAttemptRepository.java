package lithium.service.user.provider.vipps.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.vipps.domain.AuthAttempt;

public interface AuthAttemptRepository extends PagingAndSortingRepository<AuthAttempt, Long>, JpaSpecificationExecutor<AuthAttempt> {
	AuthAttempt findByXRequestId(String xRequestId);
	AuthAttempt findByCallbackRequestRequestId(String requestId);
}