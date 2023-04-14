package lithium.service.pushmsg.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.PushMsg;

public interface PushMsgRepository extends PagingAndSortingRepository<PushMsg, Long>, JpaSpecificationExecutor<PushMsg> {
	PushMsg findByProviderReferenceAndDomainProviderProviderCode(String providerReference, String providerCode);
	default PushMsg findOne(Long id) {
		return findById(id).orElse(null);
	}
}