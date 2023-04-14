package lithium.service.limit.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.limit.config.ServiceLimitConfigurationProperties;
import lithium.util.ExceptionMessageUtil;
import lithium.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PublicApiAuthenticationService {
	@Autowired private ServiceLimitConfigurationProperties properties;

	public void validate(String apiAuthorizationId, String payload, String hash)
			throws  Status470HashInvalidException, Status500InternalServerErrorException,
					Status401UnAuthorisedException {
		String expectedHash = null;

		payload += getSecretKey(apiAuthorizationId);

		try {
			expectedHash = Hash.builderMd5(payload).md5();
		} catch (Exception e) {
			throw new Status500InternalServerErrorException("Could not calculate hash "
				+ ExceptionMessageUtil.allMessages(e));
		}

		if (!expectedHash.equals(hash)) {
			log.warn("Expected hash " + expectedHash + " but received " + hash + " using payload " +
					payload);
			throw new Status470HashInvalidException();
		}
	}

	private String getSecretKey(String apiAuthorizationId) throws Status401UnAuthorisedException {
		Optional<ServiceLimitConfigurationProperties.PublicApiAuthorization> publicApiAuthorization =
			properties.getPublicApiAuthorizations().stream()
				.filter(apiAuthorization -> apiAuthorization.getId().contentEquals(apiAuthorizationId)).findFirst();
		if (publicApiAuthorization.isPresent()) {
			return publicApiAuthorization.get().getSecretKey();
		} else {
			throw new Status401UnAuthorisedException("Invalid authorization identification");
		}
	}
}
