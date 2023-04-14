package lithium.service.sms.data.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.SMS;

public interface SMSRepository extends PagingAndSortingRepository<SMS, Long>, JpaSpecificationExecutor<SMS> {
	Page<SMS> findByFailedFalseAndProcessingTrueAndProcessingStartedLessThanOrderByPriorityAscCreatedDateDesc(Date date, Pageable pageRequest);
	Page<SMS> findByFailedFalseAndProcessingFalseAndSentDateIsNullAndErrorCountLessThanOrderByPriorityAscCreatedDateDesc(int errorCountLessThan, Pageable pageRequest);
	SMS findByProviderReferenceAndDomainProviderProviderCode(String providerReference, String providerCode);
	default SMS findOne(Long id) {
		return findById(id).orElse(null);
	}
}