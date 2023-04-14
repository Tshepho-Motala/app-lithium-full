package lithium.service.user.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import lithium.exceptions.Status453EmailNotUniqueException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.data.entities.User;
import lithium.service.user.enums.TokenType;
import lithium.service.user.exceptions.Status454CellphoneNotUniqueException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserValidationBaseService {
	@Autowired @Setter UserService userService;
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired MessageSource messageSource;
	@Autowired @Setter protected MailStream mailStream;
	@Autowired @Setter protected SMSStream smsStream;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private EmailValidationService inputValidation;
  @Autowired private UserPlaceholderService userPlaceholderService;

	protected static final int MAIL_PRIORITY_HIGH = 1;
	protected static final int MAIL_PRIORITY_LOW = 2;

	protected boolean isEmail(String emailOrUsername) {
		boolean isEmail = true;
		try {
			InternetAddress emailAddr = new InternetAddress(emailOrUsername);
			emailAddr.validate();
		} catch (AddressException ex) {
			isEmail = false;
		}
		return isEmail;
	}

	protected boolean isPhoneNumber(String emailOrUsernameOrPhoneNumber) {
		boolean isphoneNumber = true;
		isphoneNumber = emailOrUsernameOrPhoneNumber.matches("^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$");
		return isphoneNumber;
	}

	protected User findUserByDomainAndEmailOrUserNameOrMobile(String domainName, String email, String username, String mobile) throws Status453EmailNotUniqueException {
		User user = null;
    List<User> returnedUsers = null;
		if ((email != null) && (!email.isEmpty()) && (isEmail(email))) {
			returnedUsers = userService.findByDomainNameAndEmail(domainName, email);
      if (returnedUsers.isEmpty()) {
        if (cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
          Optional<String> root = cachingDomainClientService.findEcosystemRootByDomainName(domainName);
          if (!root.isEmpty()) {
            returnedUsers = userService.findByDomainNameAndEmail(root.get(), email);
          }
        } else {
          returnedUsers = userService.findByDomainNameAndEmail(domainName, email);
        }
      }
		} else if(username != null && !username.isEmpty()) {
      User resetPasswordUser = userService.findByDomainNameAndUsername(domainName, username);
      if (resetPasswordUser == null) {
        if (cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
          Optional<String> root = cachingDomainClientService.findEcosystemRootByDomainName(domainName);
          if (!root.isEmpty()) {
            resetPasswordUser = userService.findByDomainNameAndUsername(root.get(), username);
            List<User> userList = new ArrayList<>();
            userList.add(resetPasswordUser);
            returnedUsers = userList;
          }
        }
      } else {
        List<User> userList = new ArrayList<>();
        userList.add(resetPasswordUser);
        returnedUsers = userList;
      }
    } else if(mobile != null && !mobile.isEmpty()) {
      returnedUsers = userService.findByDomainNameAndMobile(domainName, mobile);
      if (returnedUsers.isEmpty() && cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
        Optional<String> root = cachingDomainClientService.findEcosystemRootByDomainName(domainName);
        if (!root.isEmpty()) {
          returnedUsers = userService.findByDomainNameAndMobile(root.get(), mobile);
        }
      }
    }

    if (returnedUsers == null || returnedUsers.size() == 0) {
      log.debug("Could not find any users");
      return null;
    }

    Boolean allowDuplicateEmail = Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(domainName, DomainSettings.ALLOW_DUPLICATE_EMAIL));
    if (returnedUsers.size() == 1) {
      user = returnedUsers.get(0);
    } else if (returnedUsers.size() > 1 && allowDuplicateEmail) { //TODO :: When duplicate emails are enabled in future this needs to be addressed https://jira.livescore.com/browse/PLAT-3403
      if(username != null || !username.isEmpty()) {
        user= returnedUsers.stream().filter(x -> x.getUsername().equals(username)).findFirst().get();
      } else if(email != null || !email.isEmpty()) {
        user = returnedUsers.stream().filter(x -> x.getEmail().equals(email)).findFirst().get();
      } else if(mobile != null || !mobile.isEmpty()) {
        user = returnedUsers.stream().filter(x -> x.getCellphoneNumber().equals(mobile)).findFirst().get();
      }

    } else if(!allowDuplicateEmail){
      log.error("Find by domain name and e-mail address returned more than one user for domain with name (" + domainName + ") and e-mail address (" + email + ")");
      throw new Status453EmailNotUniqueException(RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, domainName));
    }
		return user;
	}

	protected User findUserByDomainAndEmail(String domainName, String email, boolean exception) throws Exception {
		User user = null;
		if ((email != null) && (!email.isEmpty()) && (isEmail(email))) {
			List<User> returnedUsers = userService.findByDomainNameAndEmail(domainName, email);
			if (returnedUsers.isEmpty()) {
				log.debug("Could not find any users by domain name (" + domainName + ") and email address (" + email + ")");
				if (exception) throw new Exception("Could not find any users by domain name (" + domainName + ") and email address (" + email + ")");
			}
			if (returnedUsers.size() == 1) {
				user = returnedUsers.get(0);
			} else {
				log.debug("Find by domain name and e-mail address returned more than one user for domain with name (" + domainName + ") and e-mail address (" + email + ")");
				if (exception) throw new Exception("Find by domain name and e-mail address returned more than one user for domain with name (" + domainName + ") and e-mail address (" + email + ")");
			}
		}
		return user;
	}

	protected User findUserByDomainAndPhoneNumberOrNull(String domainName, String phoneNumber) throws Status454CellphoneNotUniqueException {
		User user = null;
		if ((phoneNumber != null) && (!phoneNumber.isEmpty()) && (isPhoneNumber(phoneNumber))) {
			List<User> returnedUsers = userService.findByDomainNameAndMobile(domainName, phoneNumber);
			if (returnedUsers.isEmpty()) {
				log.debug("Could not find any users by domain name (" + domainName + ") and cellphone (" + phoneNumber + ")");
				return null;
			}
			if (returnedUsers.size() == 1) {
				user = returnedUsers.get(0);
			} else {
				log.error("Find by domain name and cellphone number returned more than one user for domain with name (" + domainName + ") and cellphone number (" + phoneNumber + ")");
				throw new Status454CellphoneNotUniqueException(RegistrationError.CELLPHONE_NOT_UNIQUE.getResponseMessageLocal(messageSource, domainName));
			}
		}
		return user;
	}
	protected User findUserByDomainAndPhoneNumber(String domainName, String phoneNumber, boolean exception) throws Exception {
		User user = null;
		if ((phoneNumber != null) && (!phoneNumber.isEmpty()) && (isPhoneNumber(phoneNumber))) {
			List<User> returnedUsers = userService.findByDomainNameAndMobile(domainName, phoneNumber);
			if (returnedUsers.isEmpty()) {
				//Adding this username lookup in here to avoid an issue where a username matches a phone number signature
				user = userService.findByDomainNameAndUsername(domainName, phoneNumber);
				if (user == null) {
					log.debug("Could not find any users by domain name (" + domainName + ") and cellphone number (" + phoneNumber + ")");
					if (exception) throw new Exception("Could not find any users by domain name (" + domainName + ") and cellphone number (" + phoneNumber + ")");
				}
			}
			if (returnedUsers.size() == 1) {
				user = returnedUsers.get(0);
			} else {
				log.debug("Find by domain name and cellphone number returned more than one user for domain with name (" + domainName + ") and cellphone number (" + phoneNumber + ")");
				if (exception) throw new Exception("Find by domain name and cellphone number returned more than one user for domain with name (" + domainName + ") and cellphone number (" + phoneNumber + ")");
			}
		}
		return user;
	}

	protected User findUserByDomainAndUsernameOrNull(String domainName, String username) {
		User user = null;
		if ((username != null) && (!username.isEmpty())) {
			user = userService.findByDomainNameAndUsername(domainName, username);
		}
		if (user == null) log.debug("Could not find user by domain name (" + domainName + ") and username (" + username + ")");
		return user;
	}
	protected User findUserByDomainAndUsername(String domainName, String username, boolean exception) throws Exception {
		User user = userService.findByDomainNameAndUsername(domainName, username);
		if (user == null) {
			log.debug("Could not find user by domain name (" + domainName + ") and username (" + username + ")");
			if (exception) throw new Exception("Could not find user by domain name (" + domainName + ") and username (" + username + ")");
		}
		return user;
	}

	protected User findUserByDomainAndEmailOrUsernameOrPhoneNumber(String domainName, String emailOrUsernameOrPhoneNumber) throws Exception {
		User user;
		if(domainName == null || emailOrUsernameOrPhoneNumber == null) {
		  return null;
    }
	  if(inputValidation.isEmail(emailOrUsernameOrPhoneNumber)) {
      user = findUserByDomainAndEmail(domainName, emailOrUsernameOrPhoneNumber, true);
    } else if(inputValidation.isPhoneNumber(emailOrUsernameOrPhoneNumber)) {
      user = findUserByDomainAndPhoneNumber(domainName, emailOrUsernameOrPhoneNumber, true);
    } else {
      user = findUserByDomainAndUsername(domainName, emailOrUsernameOrPhoneNumber, true);
    }
		return user;
	}

	protected Set<Placeholder> constructBasicPlaceholders(Domain domain, User user) throws Exception {
    Set<Placeholder> placeholders = userPlaceholderService.getPlaceholdersWithExternalData(user, domain);
    placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
    return placeholders;
  }

	protected String generateEmailToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String token = RandomStringUtils.random(5, "0123456789ABCDEFX");
		byte[] bytes = token.getBytes("UTF-8");
		return Base64.getEncoder().encodeToString(bytes);
	}
	protected String generateNumericEmailToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String token = RandomStringUtils.random(5, "0123456789");
		byte[] bytes = token.getBytes("UTF-8");
		return Base64.getEncoder().encodeToString(bytes);
	}

	protected String generateMobileToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return RandomStringUtils.random(5, "0123456789");
	}

	protected String generateToken(TokenType tokenType, Integer tokenlength) {
		String token = "";
		switch (tokenType) {
			case NUMERIC:
				token = RandomStringUtils.randomNumeric(tokenlength);
				break;
			case ALPHANUMERIC:
			default:
				token = RandomStringUtils.randomAlphanumeric(tokenlength);
				break;
		}
		return token.toUpperCase();
	}
}
