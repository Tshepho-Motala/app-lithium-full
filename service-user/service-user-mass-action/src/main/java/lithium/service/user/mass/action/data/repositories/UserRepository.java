package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.Domain;
import lithium.service.user.mass.action.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByGuid(String guid);
}