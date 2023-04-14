package lithium.service.report.games.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.report.games.data.entities.StringValue;
import lithium.service.report.games.data.repositories.StringValueRepository;

@Service
public class StringValueService {

	@Autowired StringValueRepository repo;

	@Retryable(backoff=@Backoff(maxDelay=5000, random=true), maxAttempts=40)
	public StringValue link(String value) {
		if (value == null) value = "";
		
		StringValue stringValue = repo.findByValue(value);
		if (stringValue == null) {
			stringValue = StringValue.builder().users(1).value(value).build();
			repo.save(stringValue);
		}
//		} else {
//			stringValue.setUsers(stringValue.getUsers() + 1);
//		}
		return stringValue;
	}
	
//	@Retryable
//	public void unlink(StringValue value) {
//		if (value == null) return;
//		
//		StringValue stringValue = repo.findOne(value.getId());
//		stringValue.setUsers(stringValue.getUsers() - 1);
//		if (stringValue.getUsers() <= 0L) {
//			repo.delete(stringValue);
//		} else {
//			repo.save(stringValue);
//		}
//	}
}