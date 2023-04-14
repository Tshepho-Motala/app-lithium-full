package lithium.service.stats.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByGuid(String guid);
}