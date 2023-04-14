package lithium.service.access.data.repositories;

import lithium.service.access.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByGuid(String userGuid);

	default User findOrCreate(final String userGuid) {
		User user = findByGuid(userGuid);
		if (user == null) {
			user = save(User.builder().guid(userGuid).build());
		}
		return user;
	}
}
