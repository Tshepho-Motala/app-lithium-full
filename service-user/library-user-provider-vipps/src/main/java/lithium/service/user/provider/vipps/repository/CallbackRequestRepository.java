package lithium.service.user.provider.vipps.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.vipps.domain.CallbackRequest;

public interface CallbackRequestRepository extends PagingAndSortingRepository<CallbackRequest, Long>, JpaSpecificationExecutor<CallbackRequest> {
	CallbackRequest findByRequestId(String requestId);
}