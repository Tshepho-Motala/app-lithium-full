package lithium.service.user.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserDataEntry;

public interface UserDataEntryRepository extends PagingAndSortingRepository<UserDataEntry, Long> {

	public UserDataEntry findByUserAndDataKey(User user, String key);
	
}