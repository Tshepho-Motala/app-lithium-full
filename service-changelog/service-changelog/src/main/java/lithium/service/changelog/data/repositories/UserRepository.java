package lithium.service.changelog.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.changelog.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	User findByGuid(String guid);

}
