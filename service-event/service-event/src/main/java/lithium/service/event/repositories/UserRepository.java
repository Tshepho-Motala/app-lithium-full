package lithium.service.event.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.event.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	User findByGuid(String guid);

}
