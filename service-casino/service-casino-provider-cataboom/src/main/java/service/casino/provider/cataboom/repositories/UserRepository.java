package service.casino.provider.cataboom.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import service.casino.provider.cataboom.entities.User;

@Component
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	User findByPlayerGuid(String playerguid);
}