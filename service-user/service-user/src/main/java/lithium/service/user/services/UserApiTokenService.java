package lithium.service.user.services;

import java.util.UUID;
import javax.transaction.Transactional;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.data.repositories.UserApiTokenRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserApiTokenService {
	private static final int MAX_SHORT_GUID_LENGTH = 15;
	private static final int POSTPEND_NUMBER_LENGTH = 4;

	@Autowired @Setter UserApiTokenRepository userApiTokenRepo;
	@Autowired @Setter ServiceUserConfigurationProperties properties;
	@Autowired @Setter UserService userService;

	public UserApiToken getApiTokenIfValid(
		String guid,
		String apiToken
	) {
		UserApiToken token = userApiTokenRepo.findByGuid(guid);
		if(token != null && apiToken != null) {
			if(token.getToken().contentEquals(apiToken)) {
				return token;
			}
		}
		
		return null;
	}
	
	public String findOrGenerateApiToken(String guid) {
		log.debug("findOrGenerateApiToken("+guid+")");
		UserApiToken token = userApiTokenRepo.findByGuid(guid);
		log.debug("token : "+token+"");
		if (token != null) {
			return token.getToken();
		}
		log.debug("notfound, generating..");
		String generatedToken = generateApiToken();
		log.debug("generatedToken :: "+generatedToken);
		return generatedToken;
	}

  @Transactional
  @Retryable(backoff=@Backoff(100), maxAttempts=5)
  public String findOrGenerateShortGuid(String guid) {
    log.debug("findOrGenerateShortGuid("+guid+")");
    UserApiToken token = userApiTokenRepo.findByGuid(guid);
    log.debug("token : "+token+"");
    if (token != null && token.getShortGuid() != null) {
      return token.getShortGuid();
    }
    log.debug("notfound shortGuid, generating..");
    if (token == null) {
      token = saveApiToken(guid, generateApiToken());
    } else {
      token.setShortGuid(generateShortGuid(token.getUser()));
      token = userApiTokenRepo.save(token);
    }
    log.debug("generatedToken :: "+token.getShortGuid());
    return token.getShortGuid();
  }

	@Retryable(backoff=@Backoff(100), maxAttempts=5)
  @Transactional
	public UserApiToken saveApiToken(
		String guid,
		String apiToken
	) {
		log.debug("saveApiToken("+guid+", "+apiToken+")");
		UserApiToken token = userApiTokenRepo.findByGuid(guid);
		if (token != null) {
			log.debug("existing token : "+token+"");
			if (token.getToken().contentEquals(apiToken)) {
				log.debug("token stays the same.. do nothing..");
				//do nothing
			} else {
				log.debug("updating existing token.. "+apiToken);
				token.setToken(apiToken);
				if (token.getShortGuid() == null) {
					token.setShortGuid(generateShortGuid(token.getUser()));
				}
				token = userApiTokenRepo.save(token);
			}
		} else {
			User user = userService.findFromGuid(guid);
			token = UserApiToken.builder()
					.guid(guid)
					.token(apiToken)
					.shortGuid(generateShortGuid(user))
					.user(user)
					.build();
			log.debug("new token ::  "+token+", saving.. ");
			token = userApiTokenRepo.save(token);
			user.setUserApiToken(token);
			user = userService.save(user);
		}
		return token;
	}

	public String generateApiToken() {
		return UUID.randomUUID().toString(); 
	}
	
	public UserApiToken findByToken(final String token) {
		return userApiTokenRepo.findByToken(token);
	}

	public UserApiToken findByShortGuid(final String shortGuid) {
		return userApiTokenRepo.findByShortGuid(shortGuid);
	}
	
	/*
	 *	Lets try to get a code that resembles the user in some way that is easy to remember but we test for uniqueness.
	 *	Ie, if the user is Johan van den Berg,
	 *	lets try JOHAN,
	 *	then JOHANVDB,
	 *	then JVDB,
	 *	then JBERG, then if all fail due to uniqueness,
	 *	try JOHANXXX or any of the other where XXX is a random number.
	 */
	private String generateShortGuid(final User user) {
//		User user = getUserFromGuid(userGuid);

		try {

			String firstName = ((user.getFirstName() != null) ? user.getFirstName() : ((user.getUsername() != null) ? user.getUsername() : RandomStringUtils.randomAlphabetic(5)));
			String lastName = ((user.getLastName() != null) ? user.getLastName() : ((user.getUsername() != null) ? user.getUsername() : RandomStringUtils.randomAlphabetic(5)));

			String[] firstNameSplit = firstName.split(" ");
			String[] lastNameSplit = lastName.split(" ");

			String shortGuid = firstNameSplit[0].toUpperCase();

			boolean unique = false;

			if (shortGuid.length() > MAX_SHORT_GUID_LENGTH) {
				shortGuid = shortGuid.substring(0, MAX_SHORT_GUID_LENGTH);
			}
			unique = isShortGuidUnique(shortGuid);
			if (!unique) {
				String ln = "";
				for (int i = 0; i < lastNameSplit.length; i++) {
					if (lastNameSplit[i].length() > 0)
						ln += lastNameSplit[i].substring(0, 1);
				}
				if ((shortGuid.length() + ln.length()) > MAX_SHORT_GUID_LENGTH) {
					shortGuid = shortGuid.substring(0, shortGuid.length() - ln.length());
				}
				shortGuid += ln;
				unique = isShortGuidUnique(shortGuid);
			}
			if (!unique) {
				shortGuid = "";
				for (int i = 0; i < firstNameSplit.length; i++) {
					if (firstNameSplit[i].length() > 0)
						shortGuid += firstNameSplit[i].substring(0, 1);
				}
				for (int i = 0; i < lastNameSplit.length; i++) {
					if (lastNameSplit[i].length() > 0)
						shortGuid += lastNameSplit[i].substring(0, 1);
				}
				unique = isShortGuidUnique(shortGuid);
			}
			if (!unique) {
				shortGuid = firstNameSplit[0].substring(0, 1);
				shortGuid += lastNameSplit[lastNameSplit.length - 1];
				if (shortGuid.length() > MAX_SHORT_GUID_LENGTH) {
					shortGuid = shortGuid.substring(0, MAX_SHORT_GUID_LENGTH);
				}
				unique = isShortGuidUnique(shortGuid);
			}
			int count = 0;
			while (!unique) {
				if (count++ > 10) throw new Exception("Gave up after 10 attempts");
				shortGuid = firstNameSplit[0];
				if (shortGuid.length() > (MAX_SHORT_GUID_LENGTH - POSTPEND_NUMBER_LENGTH)) {
					shortGuid = shortGuid.substring(0, (MAX_SHORT_GUID_LENGTH - POSTPEND_NUMBER_LENGTH));
				}
				shortGuid = shortGuid + RandomStringUtils.random(POSTPEND_NUMBER_LENGTH, false, true);
				unique = isShortGuidUnique(shortGuid);
			}
			return shortGuid.toUpperCase();
		} catch (Throwable e) {
			log.error("Exception during the generation of short guid. Returning random. " + user, e);
			return RandomStringUtils.randomAlphabetic(14).toUpperCase();
		}
		
	}
	
	// FIXME: 	It would be really nice to have an index on short_guid,
	//			but it is a nullable column and there is existing data.
	private boolean isShortGuidUnique(String shortGuid) {
		return !(userApiTokenRepo.findByShortGuid(shortGuid.toUpperCase()) != null);
	}

	public UserApiToken findByGuid(String guid) {
		return userApiTokenRepo.findByGuid(guid);
	}
}
