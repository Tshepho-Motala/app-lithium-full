package lithium.service.user.provider.vipps.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.vipps.domain.ErrorInfo;

public interface ErrorInfoRepository extends PagingAndSortingRepository<ErrorInfo, Long>, JpaSpecificationExecutor<ErrorInfo> {
	
}