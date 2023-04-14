package lithium.service.user.provider.vipps.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.vipps.domain.UserDetails;

public interface UserDetailsRepository extends PagingAndSortingRepository<UserDetails, Long>, JpaSpecificationExecutor<UserDetails> {
	
}