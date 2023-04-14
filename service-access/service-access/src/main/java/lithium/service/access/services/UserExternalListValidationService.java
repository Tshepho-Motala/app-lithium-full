package lithium.service.access.services;

import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.User;
import lithium.service.access.data.entities.UserExternalListValidation;
import lithium.service.access.data.repositories.UserExternalListValidationRepository;
import lithium.service.access.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserExternalListValidationService {
	@Autowired UserRepository userRepository;
	@Autowired UserExternalListValidationRepository repository;

	public UserExternalListValidation find(
		String userGuid,
		ExternalList externalList
	) {
		return repository.findByUserAndExternalList(userRepository.findOrCreate(userGuid), externalList);
	}

	public UserExternalListValidation updateOrCreate(
		String userGuid,
		ExternalList externalList,
		Boolean passed,
		String message,
		String errorMessage
	) {
		UserExternalListValidation userExternalListValidation = find(userGuid, externalList);
		if (userExternalListValidation != null) {
			userExternalListValidation = update(
				userExternalListValidation,
				passed,
				message,
				errorMessage
			);
		} else {
			userExternalListValidation = add(
				userGuid,
				externalList,
				passed,
				message,
				errorMessage
			);
		}
		return userExternalListValidation;
	}

	private UserExternalListValidation add(
		String userGuid,
		ExternalList externalList,
		Boolean passed,
		String message,
		String errorMessage
	) {
		User user = userRepository.findOrCreate(userGuid);
		return add(user, externalList, passed, message, errorMessage);
	}

	private UserExternalListValidation add(
		User user,
		ExternalList externalList,
		Boolean passed,
		String message,
		String errorMessage
	) {
		UserExternalListValidation userExternalListValidation = UserExternalListValidation.builder()
		.user(user)
		.externalList(externalList)
		.passed(passed)
		.message(message)
		.errorMessage(errorMessage)
		.build();
		userExternalListValidation = repository.save(userExternalListValidation);
		return userExternalListValidation;
	}

	public UserExternalListValidation update(
		UserExternalListValidation userExternalListValidation,
		Boolean passed,
		String message,
		String errorMessage
	) {
		userExternalListValidation.setPassed(passed);
		userExternalListValidation.setMessage(message);
		userExternalListValidation.setErrorMessage(errorMessage);
		userExternalListValidation.setUpdatedOn(new Date());
		userExternalListValidation = repository.save(userExternalListValidation);
		return userExternalListValidation;
	}
}
