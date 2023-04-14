package lithium.service.sms.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByGuid(String guid);
}