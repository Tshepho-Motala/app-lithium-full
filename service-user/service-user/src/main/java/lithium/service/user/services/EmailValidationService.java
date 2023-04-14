package lithium.service.user.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.client.objects.placeholders.PlaceholderBuilder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.EmailValidationToken;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.EmailValidationTokenRepository;
import lithium.service.user.enums.TokenType;
import lithium.service.user.exceptions.Status406InvalidValidationTokenException;
import lithium.service.user.exceptions.Status420InvalidEmailException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.validators.UserValidatorProperties;
import lithium.util.ExceptionMessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class EmailValidationService extends UserValidationBaseService {
	@Autowired EmailValidationTokenRepository emailValidationTokenRepository;
	@Autowired @Setter MessageSource messageSource;
	@Autowired ChangeLogService changeLogService;
	@Autowired
  ServiceUserConfigurationProperties properties;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private DomainService domainService;
  @Autowired
  private ModelMapper modelMapper;
  @Autowired PubSubUserService pubSubUserService;

	public void sendEmailValidationTokenEmail(String domainName, String emailOrUsername, boolean emailAddressChanged, boolean resend, boolean emailValidated) throws Exception {
		String token = generateEmailToken();
		if (properties.getEmailValidationToken().getVersion() == 2) {
			token = generateToken(TokenType.ALPHANUMERIC, 35);
		}
		sendEmailValidationTokenEmailInternal(domainName, emailOrUsername, emailAddressChanged, resend, token, emailValidated);
	}

	public void sendEmailValidationTokenEmailNumericToken(String domainName, String emailOrUsername, boolean emailAddressChanged, boolean resend, boolean emailValidated) throws Exception {
		sendEmailValidationTokenEmailInternal(domainName, emailOrUsername, emailAddressChanged, resend, generateMobileToken(), emailValidated);
	}

	private void sendEmailValidationTokenEmailInternal(String domainName, String emailOrUsername, boolean emailAddressChanged, boolean resend, String generatedToken,
      boolean emailValidated) throws Exception {
		try {
			sendEmailValidationTokenEmailInternalECE(domainName, emailOrUsername, emailAddressChanged, resend, generatedToken, emailValidated);
		} catch (Exception e) {
			throw e;
		}
	}
	private String sendEmailValidationTokenEmailInternalECE(
		String domainName, String emailOrUsername, boolean emailAddressChanged, boolean resend, String generatedToken, boolean emailValidated
	) throws
		LithiumServiceClientFactoryException,
		Status404UserNotFoundException,
		Status500InternalServerErrorException
	{
    StringBuilder logMsg = new StringBuilder(" sendEmailValidationTokenEmailInternal :: ");
    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Locale locale = LocaleContextHolder.getLocale();
    logMsg.append(" locale: ").append(locale);

    User user;
    boolean pendingEmailValidationActivate = Boolean.parseBoolean(
        cachingDomainClientService.getDomainSetting(domainName, DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));

    if (!isValidIEmailAddress(emailOrUsername)) {
      log.warn("User email validation failed, the email(" + emailOrUsername + ") is incorrect.");
      throw new Status420InvalidEmailException(RegistrationError.INVALID_EMAIL.getResponseMessageLocal(messageSource, domainName));
    }

    try {
      user = getUserForEmailValidation(domainName, emailOrUsername,  pendingEmailValidationActivate, locale);
      if (ObjectUtils.isEmpty(user)) {
				throw new Exception();
			}
		} catch (Exception e) {
      logMsg.append(" - user not found!");
      log.error(logMsg.toString(), e);
			throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.ACCOUNT_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domain.getName())}, "Player account could not be found.", locale), e.getStackTrace());
		}

    logMsg.append(" user(").append(user.guid()).append(") ");
    List<EmailValidationToken> tokens = emailValidationTokenRepository.findByUser(user);

    if (!ObjectUtils.isEmpty(tokens)) {
      tokens.forEach(token -> {
        if(token.getEmail().equals(emailOrUsername)){
          logMsg.append("existing token: ").append(token.getToken());
          emailValidationTokenRepository.delete(token);
        }
      });
    }

    EmailValidationToken token = new EmailValidationToken(generatedToken, user, new Date(), emailOrUsername);
    logMsg.append(" new token: ").append(generatedToken);
    emailValidationTokenRepository.save(token);

		String emailTemplate;
		if (resend) {
			emailTemplate = "email.validation.resend";
		} else if (emailAddressChanged) {
			emailTemplate = "email.validation.emailaddress.changed";
		} else {
			emailTemplate = "email.validation";
		}
    logMsg.append(" templ: ").append(emailTemplate);

    Set<Placeholder> placeholders;
		try {
			placeholders = constructBasicPlaceholders(domain, user);
		} catch (Exception e) {
      logMsg.append(" - problem constructing basic placeholders.");
      log.error(logMsg.toString(), e);
			throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
		}
		placeholders.add(
        PlaceholderBuilder.USER_EMAIL_VALIDATE_URL.from(domain.getUrl() + "?action=emailvalidation" + (resend? "&resend=" + resend : "") + "&user=" + emailOrUsername + "&token=" + token.getToken()));
		placeholders.add(PlaceholderBuilder.USER_VALIDATE_TOKEN.from(token.getToken()));
		placeholders.add(PlaceholderBuilder.USER_PENDING_VALIDATION_EMAIL_ADDRESS.from(emailOrUsername));

    if(!emailValidated) {
      mailStream.process(
          EmailData.builder()
              .authorSystem()
              .emailTemplateName(emailTemplate)
              .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
              .to(emailOrUsername)
              .priority(MAIL_PRIORITY_HIGH)
              .userGuid(user.guid())
              .placeholders(placeholders)
              .domainName(domainName)
              .build()
      );
      logMsg.append(" sending validation email. ");
    }
		try {
			if (!user.isWelcomeEmailSent()) logMsg.append(" sending welcome email. ");
			sendWelcomeMail(user, domain);
		} catch (Exception e) {
			logMsg.append(" - problem sending welcome email");
      log.error(logMsg.toString(), e);
			throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
		}
		return logMsg.toString();
	}

  private User getUserForEmailValidation(String domainName, String emailOrUsername, boolean pendingEmailValidationActivate, Locale locale) throws Exception {

    if (!pendingEmailValidationActivate) {
      return findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsername);
    }

    List<User> existingUsers;

    if (cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
      existingUsers = userService.findByDomainNameAndLabelEcosystemAware(domainName, Label.PENDING_EMAIL, emailOrUsername);
    } else {
      existingUsers = userService.findByDomainNameAndLabelAndLabelValue(domainName, Label.PENDING_EMAIL, emailOrUsername);
    }

    if (ObjectUtils.isEmpty(existingUsers)) {
      return findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsername);
    }

    if (existingUsers.size() == 1) {
      return existingUsers.get(0);
    }

    for (User user1 : existingUsers) {
      if (user1.domainName().equals(domainName)) {
        return user1;

      }
    }

    throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.ACCOUNT_NOT_FOUND",
        new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Player account could not be found.", locale));

  }

  /**
	 * Send a welcome email if one has not already been sent
	 * @param user
	 * @return Response.Status
	 * @throws Exception
	 */
	private Response.Status sendWelcomeMail(final User user) throws Exception {
		return sendWelcomeMail(user, modelMapper.map(user.getDomain(), Domain.class));
	}
	private Response.Status sendWelcomeMail(final User user, Domain domain) throws Exception {
	  //This is for any non ecosystem domain user registrations
	  if(!cachingDomainClientService.isDomainInAnyEcosystem(domain.getName())){
      sendEmail(user, domain);
	    return Status.OK;
    }

    Optional<String> rootDomainName = cachingDomainClientService.findEcosystemRootByDomainName(user.domainName());
    //This is for when we are registering a mutual exclusive or an ecosystem member domain user
    if (rootDomainName.isPresent() && !ObjectUtils.nullSafeEquals(user.getDomain().getName(), rootDomainName.get())) {
      sendEmail(user, domain);
      return Status.OK;
    }

    Optional<EcosystemDomainRelationship> ecosystemName = cachingDomainClientService.findEcosystemNameByEcosystemRootDomainName(domain.getName());
    if (ecosystemName.isPresent()) {
      Optional<String> exclusiveDomainName = domainService.getRegisteredExclusiveDomainName(ecosystemName.get().getEcosystem().getName(),
          user.getEmail());
      //This is when we are registering only the root domain user (LSM) meaning there has not been any mutually exclusive registrations
      if (exclusiveDomainName.isEmpty()) {
        sendEmail(user, domain);
        return Status.OK;
      }

      Optional<EcosystemDomainRelationship> enabledDomainInEcosystem = cachingDomainClientService.findEnabledDomainsInEcosystem(
          ecosystemName.get().getEcosystem().getName(), exclusiveDomainName.get());

      if (enabledDomainInEcosystem.isPresent()) {
        //This is when we automatically generate a root account, so we check the mutually exclusive domain if it should get an email
        if (!enabledDomainInEcosystem.get().getDisableRootWelcomeEmail()) {
          sendEmail(user, domain);
        }
        return Status.OK;
      }
    }

    return Status.BAD_REQUEST;
	}

  public Status validateTokenAndSetEmailValidated(String domainName, String emailOrUsername, Boolean resend, String token) throws Exception {
    log.info("Email validation request: email (" + emailOrUsername + ") token (" + token + ")");
    boolean pendingEmailValidationActivate = Boolean.parseBoolean(
        cachingDomainClientService.getDomainSetting(domainName, DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));
    User user = getUserForEmailValidation(domainName, emailOrUsername,  pendingEmailValidationActivate, LocaleContextHolder.getLocale());

    if (user.isEmailValidated() && !pendingEmailValidationActivate) {
      log.warn("Email address (" + user.getEmail() + ") for user (" + user.getUsername() + ") is already validated.");
      return Status.CONFLICT;
    }

    EmailValidationToken validToken = emailValidationTokenRepository.findByUserAndEmail(user, emailOrUsername);
    if (validToken != null && validToken.getToken().equals(token)) {
      user = userService.saveEmailValidated(user.getId(), true, validToken.getEmail(), emailOrUsername, pendingEmailValidationActivate);
      emailValidationTokenRepository.delete(validToken);
      return sendWelcomeMail(user);
    } else {
      log.warn("Could not validate email address (" + user.getEmail() + ") for user (" + user.getUsername() + "). Token (" + token + ") is invalid");
      return Status.INVALID_DATA;
    }
  }

  public void step1(String domainName, String emailOrUsername) throws Status500InternalServerErrorException, Status404UserNotFoundException {
    String logMsg = "Email Validation Step 1 :: ";
    logMsg += "dn: "+domainName+" eup: "+emailOrUsername;
    try {
      logMsg += sendEmailValidationTokenEmailInternalECE(domainName, emailOrUsername, false, true, generateToken(TokenType.ALPHANUMERIC, 35), false);
    } catch (Status404UserNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
    }
    log.info(logMsg);
  }

  public void step2(String domainName, String emailOrUsername, String validationToken) throws
    Status404UserNotFoundException,
    Status406InvalidValidationTokenException,
    Status500InternalServerErrorException
  {
    String logMsg = "Email Validation Step 2 :: ";
    logMsg += "dn: "+domainName+" eup: "+emailOrUsername+" vt: "+validationToken;
    try {
      Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Locale locale = LocaleContextHolder.getLocale();
      logMsg += " locale: " + locale;
      boolean pendingEmailValidationActivate = Boolean.parseBoolean(
          cachingDomainClientService.getDomainSetting(domainName, DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));
      User user = getUserForEmailValidation(domainName, emailOrUsername, pendingEmailValidationActivate, locale);

      EmailValidationToken evt = emailValidationTokenRepository.findByUserAndEmail(user, emailOrUsername);

			if (user.isEmailValidated() && !pendingEmailValidationActivate) {
				logMsg += " user(" + user.guid() + ") email already validated.";
				log.warn(logMsg);
				if (evt != null) {
					logMsg += " evt: " + evt.toStringEmailValidationTokenWithUserGuid();
					emailValidationTokenRepository.delete(evt);
					logMsg += " Token deleted. Exit";
				}
				return;
			}

      if ((evt != null) && (evt.getToken().equals(validationToken))) {
        user = userService.saveEmailValidated(user.getId(), true, evt.getEmail(), emailOrUsername, pendingEmailValidationActivate);

        logMsg += " evt: " + evt.toStringEmailValidationTokenWithUserGuid();
        emailValidationTokenRepository.delete(evt);
        logMsg += " , token deleted. ";
        logMsg += " , user(" + user.guid() + ") email validated. ";

        try{
          pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_UPDATE);
        }catch (Exception e){
          log.warn("can't sent pub-sub message" + e.getMessage() +" | user: "+ user.guid());
        }

				List<ChangeLogFieldChange> clfc = changeLogService.copy(
					user,
					new User(),
					new String[]{
						"emailValidated"
					}
				);
				changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), user.guid(), null, "Email Validated",
            null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domainName);

				if (!user.isWelcomeEmailSent()) logMsg += " , sending welcome email. ";
				sendWelcomeMail(user, domain);
			} else {
				logMsg += " - invalid token!";
				throw new Status406InvalidValidationTokenException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.INVALID_TOKEN_SUPPLIED", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Invalid validation token supplied.", locale));
			}
		} catch (Status404UserNotFoundException | Status406InvalidValidationTokenException e) {
			log.warn(logMsg);
			throw e;
		} catch (Exception e) {
			log.error(logMsg, e);
			throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
		}
		log.info(logMsg);
	}

  private void sendEmail(User user, Domain domain) throws Exception {
    if (!user.isWelcomeEmailSent()) {
      log.error("Sending welcome email to (" + user.getDomain().getName() + ")");
      if (domain == null) {
        domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
      }
      Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
      mailStream.process(
          EmailData.builder()
              .authorSystem()
              .emailTemplateName("welcome")
              .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
              .to(user.getEmail())
              .priority(MAIL_PRIORITY_HIGH)
              .userGuid(user.guid())
              .placeholders(placeholders)
              .domainName(domain.getName())
              .build()
      );
      user.setWelcomeEmailSent(true);
      userService.saveEmailWelcomeStatus(user.getId(), true);
    } else {
      log.debug("Welcome email has already been sent to (" + user.getUsername() + ")");
    }
  }

  public void sendAccountDeletionEmail(Set<Placeholder> placeholders, String playerEmail, String playerGuid, String domainName) {
    mailStream.process(
        EmailData.builder()
            .emailTemplateName("profile.delete")
            .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
            .to(playerEmail)
            .priority(MAIL_PRIORITY_HIGH)
            .userGuid(playerGuid)
            .authorGuid(playerGuid)
            .placeholders(placeholders)
            .domainName(domainName)
            .build()
    );
  }

  private boolean isValidIEmailAddress(String email) {
    Matcher matcher = UserValidatorProperties.EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }
}
