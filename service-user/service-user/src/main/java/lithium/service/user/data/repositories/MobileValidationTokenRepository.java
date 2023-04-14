package lithium.service.user.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.MobileValidationToken;
import lithium.service.user.data.entities.User;

public interface MobileValidationTokenRepository extends PagingAndSortingRepository<MobileValidationToken, Long> {
	public MobileValidationToken findByUser(User user);
}