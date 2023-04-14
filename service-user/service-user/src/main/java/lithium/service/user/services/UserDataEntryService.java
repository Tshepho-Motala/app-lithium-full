package lithium.service.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserDataEntry;
import lithium.service.user.data.repositories.UserDataEntryRepository;

@Service
public class UserDataEntryService {
	@Autowired UserDataEntryRepository repo;
	
	public UserDataEntry set(User user, String key, String value) {
		UserDataEntry ulv = get(user, key);
		if (ulv == null) {
			ulv = UserDataEntry.builder().user(user).dataKey(key).dataValue(value).build();
		} else {
			ulv.setDataValue(value);
		}
		return repo.save(ulv);
	}
	
	public UserDataEntry get(User user, String key) {
		UserDataEntry ulv = repo.findByUserAndDataKey(user, key);
		return ulv;
	}
}