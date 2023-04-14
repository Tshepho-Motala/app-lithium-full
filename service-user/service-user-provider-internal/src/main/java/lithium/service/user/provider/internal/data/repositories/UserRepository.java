package lithium.service.user.provider.internal.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.provider.internal.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByDomainAndUsernameIgnoreCase(String domain, String username);
}
