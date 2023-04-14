package lithium.service.user.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status493ExcessiveFailedPasswordResetBlockException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.user.client.objects.UserPasswordReset;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordToken;
import lithium.service.user.data.repositories.UserPasswordTokenRepository;
import lithium.service.user.enums.TokenType;
import lithium.service.user.enums.Type;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status424InvalidResetTokenException;
import lithium.service.user.exceptions.Status999GeneralFailureException;
import lithium.service.user.services.notify.PasswordChangeNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.ExceptionMessageUtil;
import lithium.util.PasswordHashing;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_PASSWORD_RESET_CODE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_PASSWORD_RESET_TOKEN;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_PASSWORD_RESET_URL;

@Service
@Slf4j
public class PasswordResetService extends UserValidationBaseService {
	@Value("${lithium.password.salt}")
	private String passwordSalt;

	@Autowired @Setter UserPasswordTokenRepository userPasswordTokenRepository;
  @Autowired @Setter UserPasswordHashAlgorithmService userPasswordHashAlgorithmService;
	@Autowired @Setter ChangeLogService changeLogService;
	@Autowired @Setter MessageSource messageSource;
	@Autowired @Setter PasswordChangeNotificationService passwordChangeNotificationService;
	@Autowired @Setter CachingDomainClientService cachingDomainClientService;
	@Autowired LimitInternalSystemService limitInternalSystemService;

	private Date dobValid(Integer year, Integer month, Integer day) throws Exception {
		Date dob = null;
		if ((year == null) && (month == null) && (day == null)) return null;
		DateFormat sdf = new SimpleDateFormat("yyyy.M.dd");
		sdf.setLenient(false);
		dob = sdf.parse(year + "." + month + "." + day);
		return dob;
	}
	private Date dobValid(String source, String pattern) throws Exception {
		Date dob = null;
		if ((source == null) || (pattern == null)) return null;
		DateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(false);
		dob = sdf.parse(source);
		return dob;
	}
	public void step1(
			String domainName,
			String email,
			String username,
			String mobile,
			Type type,
			TokenType tokenType,
			Integer tokenLength,
			String dateOfBirth,
      LithiumTokenUtil util
	) throws
			Status100InvalidInputDataException,
			Status405UserDisabledException,
			Status422InvalidDateOfBirthException,
			Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException,
			Status493ExcessiveFailedPasswordResetBlockException,
			Status999GeneralFailureException,
			Status496PlayerCoolingOffException
	{
		step1(null, null, domainName, email, username, mobile, type, tokenType, tokenLength, dateOfBirth, util);
	}
	
	public void step1(
		User user,
		String loggedInUserGuid,
		String domainName,
		String email,
		String username,
		String mobile,
		Type type,
		TokenType tokenType,
		Integer tokenlength,
		String dateOfBirth,
    LithiumTokenUtil util
	) throws
		Status100InvalidInputDataException,
		Status405UserDisabledException,
		Status422InvalidDateOfBirthException,
		Status490SoftSelfExclusionException,
		Status491PermanentSelfExclusionException,
		Status493ExcessiveFailedPasswordResetBlockException,
		Status999GeneralFailureException,
		Status496PlayerCoolingOffException
	{
		String parameterStr = "";

		try {
			Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Locale locale = LocaleContextHolder.getLocale();
			log.trace("locale : " + locale);

			if (tokenlength < 4) throw  new Status100InvalidInputDataException("The token length is too short");
			if (user == null) {
				//find user
				parameterStr = "Password Reset Step1 :: dn: "+domainName+", e: "+email+", u: "+username+", m: "+mobile+", t: "+type.name()+", tokenType: "+tokenType.name()+", l: "+tokenlength+", dob: "+dateOfBirth;

				user = findUserByDomainAndEmailOrUserNameOrMobile(domainName, email, username, mobile);
				if (user == null) {
					parameterStr += "  -  user not found!";
					log.warn(parameterStr);
					throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.ERROR_100", null, locale));
				}
				parameterStr += " user: " + user.guid();

				int domainFailedResetCountSetting = domainFailedResetCountSetting(domain.getName());

				if (user.getFailedResetCount() >= domainFailedResetCountSetting) {
					parameterStr += ". Domain Limit Reached.";
					log.error(parameterStr);
					throw new Status493ExcessiveFailedPasswordResetBlockException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.ERROR_493", null, locale));
				}

				if (dateOfBirth != null) {
					parameterStr += ", checking dob: ";
					Date dob = null;
					try {
						dob = dobValid(user.getDobYear(), user.getDobMonth(), user.getDobDay());
					} catch (Exception e) {
						throw e;
					}
					if (dob != null) {
						try {
							if (dob.compareTo(dobValid(dateOfBirth, "dd/MM/yyyy")) != 0) {
								parameterStr += " invalid dob received.";
								user.incFailedResetCount();
								parameterStr += ". Attempt: " + user.getFailedResetCount() + ". ";
								userService.save(user);
								log.warn(parameterStr);

								if (user.getFailedResetCount() == domainFailedResetCountSetting) {
									changeLogService.registerChangesForNotesWithFullNameAndDomain(
                    "user",
                    "comment",
                    user.getId(),
                    user.guid(),
                    null,
                    messageSource.getMessage("SERVICE_USER.PASSWORDRESET.493_BLOCKED_ATTEMPT", null, locale),
                    null,
                    null,
                    Category.ACCOUNT,
                    SubCategory.EDIT_DETAILS,
                    0,
                    domainName
									);
								}
								throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.INVALID_DOB", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Invalid date of birth specified.", locale));
							}
						} catch (Status422InvalidDateOfBirthException e) {
							throw e;
						} catch (Exception e) {
							long ms = new Date().getTime();
							parameterStr += " exception: ("+ms+") :: " + ExceptionMessageUtil.allMessages(e);
							log.error(parameterStr, e);
							throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.INVALID_DOB", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Invalid date of birth specified.", locale), e.getStackTrace());
						}
						parameterStr += " valid ";
					} else {
						parameterStr += "no player dob available. ";
					}
				}
			} else {
				dateOfBirth = user.getDobDay() + "/" + user.getDobMonth() + "/" + user.getDobYear();
				parameterStr = "Password Reset Step1 :: dn: " + domainName
						+ ", e: " + user.getEmail()
						+ ", u: " + user.getUsername()
						+ ", m: " + user.getCellphoneNumber()
						+ ", t: " + type.name()
						+ ", tokenType: " +tokenType.name()
						+ ", l: " + tokenlength
						+ ", dob: " + dateOfBirth;
				parameterStr += " valid ";
			}

			if (!user.getStatus().getUserEnabled()) {
				parameterStr += "  -  user account disabled!";
				log.warn(parameterStr);
				throw new Status405UserDisabledException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.USER_DISABLED", null, locale));
			}

			limitInternalSystemService.checkPlayerRestrictions(user.guid(), locale.toLanguageTag());

			List<UserPasswordToken> tokens = userPasswordTokenRepository.findByUser(user);
			if (!tokens.isEmpty()) {
        Optional<String> setting = domain.findDomainSettingByName(DomainSettings.PASSWORD_RESET_TIMEOUT.key());
        long timeoutValue = Long.valueOf(DomainSettings.PASSWORD_RESET_TIMEOUT.defaultValue());
        if(setting.isPresent()) {
          timeoutValue = Long.parseLong(setting.get());
        }
        long  latestTokenTime = Instant.now().minusSeconds(timeoutValue).toEpochMilli();
        for (UserPasswordToken token: tokens) {
          if (latestTokenTime <= token.getCreatedOn().toInstant().toEpochMilli()) {
            latestTokenTime = token.getCreatedOn().getTime();
          }
        }
        long timeNow = Instant.now().minusSeconds(timeoutValue).toEpochMilli();
        if (latestTokenTime > timeNow) {
          long timeRemaining = (latestTokenTime-timeNow )/1000;
          throw new Status100InvalidInputDataException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.PASSWORD_RESET " + timeRemaining + " ERROR_DICTIONARY.REGISTRATION.TIME_REMAINING"
              , new Object[] {new lithium.service.translate.client.objects.Domain(user.domainName())},
              "Token cool off period not yet reached. " + timeRemaining + " seconds remaining", LocaleContextHolder.getLocale()));
        }
				parameterStr += ", deleting existing tokens for user: " + user.guid();
				clearResetTokens(user);
			}

			parameterStr += ", generate and save new user password token: ";

			List<Type> commsTypes = new ArrayList<>();
			if (type.equals(Type.ALL)) {
				commsTypes.add(Type.EMAIL);
				commsTypes.add(Type.SMS);
			} else {
				commsTypes.add(type);
			}

			for (Type commsType: commsTypes) {
				//generate and save token
				String token = generateToken(tokenType, tokenlength);
				while (userPasswordTokenRepository.findByUserAndToken(user, token) != null) {
					token = generateToken(tokenType, tokenlength);
				}

				UserPasswordToken userPasswordToken = userPasswordTokenRepository.save(
					UserPasswordToken.builder()
					.token(token)
					.user(user)
					.type(commsType)
					.build()
				);
				parameterStr += userPasswordToken.getType().type();

				Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
				placeholders.add(USER_PASSWORD_RESET_URL.from(domain.getUrl() + "/forgotpassword?action=passwordreset&user="
					+ user.guid() + "&token=" + userPasswordToken.getToken()));
				placeholders.add(USER_PASSWORD_RESET_TOKEN.from(userPasswordToken.getToken()));
				placeholders.add(USER_PASSWORD_RESET_CODE.from(userPasswordToken.getToken()));

				switch (commsType) {
					case EMAIL:
						sendMail(user, domainName, placeholders);
						parameterStr += " email sent. ";
						break;
					case SMS:
						sendSms(user, domainName, placeholders);
						parameterStr += " sms sent. ";
						break;
				}
			}
			loggedInUserGuid = loggedInUserGuid == null ? user.guid() : loggedInUserGuid;

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), loggedInUserGuid, util,
              "Player has asked for a password reset", null, new ArrayList<ChangeLogFieldChange>(), Category.ACCOUNT,
              SubCategory.PASSWORD_RESET, 0, domainName);

      parameterStr += " note added. ";

			log.debug(parameterStr);
		} catch (
			Status100InvalidInputDataException |
			Status422InvalidDateOfBirthException |
			Status405UserDisabledException |
			Status490SoftSelfExclusionException |
			Status491PermanentSelfExclusionException |
			Status493ExcessiveFailedPasswordResetBlockException |
			Status496PlayerCoolingOffException e
		) {
			throw e;
		} catch (Exception e) {
			long ms = new Date().getTime();
			parameterStr += " exception: ("+ms+") :: " + ExceptionMessageUtil.allMessages(e);
			log.error(parameterStr, e);
			throw new Status999GeneralFailureException("An unexpected server error has occurred: "+ms);
		}
	}

	private void sendSms(User user, String domainName, Set<Placeholder> placeholders) {
		if (user.getCellphoneNumber() != null) {
			log.trace("Sending Password Reset SMS : u: "+user.guid()+"("+user.getCellphoneNumber()+")");

      smsStream.process(
				SMSBasic.builder()
					.smsTemplateName("sms.password.reset")
					.smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
					.to(user.getCellphoneNumber())
					.priority(MAIL_PRIORITY_HIGH)
					.userGuid(user.guid())
					.placeholders(placeholders)
					.domainName(domainName)
					.build()
			);
		}
	}
	private void sendMail(User user, String domainName, Set<Placeholder> placeholders) {
		if (user.getEmail() != null) {
			log.trace("Sending Password Reset Mail : u: "+user.guid()+"("+user.getEmail()+")");

      mailStream.process(
          EmailData.builder()
              .authorSystem()
              .emailTemplateName("password.reset")
              .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
              .to(user.getEmail())
              .priority(MAIL_PRIORITY_HIGH)
              .userGuid(user.guid())
              .placeholders(placeholders)
              .domainName(domainName)
              .build()
      );
		}
	}

	private int domainFailedResetCountSetting(String domainName) {
		try {
			Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
			Optional<String> setting = domain.findDomainSettingByName(DomainSettings.FAILED_PASSWD_RESET_THRESHOLD.key());
      return setting.map(Integer::parseInt).orElseGet(() -> Integer.parseInt(DomainSettings.FAILED_PASSWD_RESET_THRESHOLD.defaultValue()));
		} catch (Exception e) {
			log.error("Could not retrieve domain setting for FAILED_PASSWD_RESET_THRESHOLD", e);
			return 20;
		}
	}

  @Transactional(rollbackFor = Exception.class)
	public void step2(
		String domainName,
		String email,
		String username,
		String mobile,
		UserPasswordReset userPasswordReset,
    LithiumTokenUtil util
	) throws
		Status100InvalidInputDataException,
		Status424InvalidResetTokenException,
		Status493ExcessiveFailedPasswordResetBlockException,
		Status999GeneralFailureException
	{
		String parameterStr = "Password Reset Step2 :: dn: "+domainName+", e: "+email+", u: "+username+", m: "+mobile+", upr: "+userPasswordReset;
		User user;
		try {
			Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      Locale locale = LocaleContextHolder.getLocale();

			user = findUserByDomainAndEmailOrUserNameOrMobile(domainName, email, username, mobile);
			if (user == null) {
				parameterStr += "  -  user not found!";
				log.warn(parameterStr);
				throw new Status100InvalidInputDataException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.ERROR_100", null, locale));
			}
			parameterStr += " user: " + user.guid();

			int domainFailedResetCountSetting = domainFailedResetCountSetting(domain.getName());

			if (user.getFailedResetCount() >= domainFailedResetCountSetting) {
				parameterStr += ". Domain Limit Reached.";
				log.error(parameterStr);
				throw new Status493ExcessiveFailedPasswordResetBlockException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.ERROR_493", null, locale));
			}

			List<UserPasswordToken> userPasswordTokens = userPasswordTokenRepository.findByUser(user);
			UserPasswordToken userPasswordToken = null;
			for (UserPasswordToken upt: userPasswordTokens) {
				if (upt.getToken().contentEquals(userPasswordReset.getToken())) {
					userPasswordToken = upt;
					break;
				}
			}

			if (userPasswordToken != null && userPasswordToken.getToken().equals(userPasswordReset.getToken())) {
				Type userPasswordTokenType = null;
				if (userPasswordToken.getType() != null) userPasswordTokenType = Type.fromType(userPasswordToken.getType().type());
        String userLastNamePrefix = user.getLastNamePrefix();
				String userFullName = userLastNamePrefix != null ? user.getFirstName() + " " + userLastNamePrefix + " " + user.getLastName() :
            user.getFirstName() + " " + user.getLastName();
//        userFullName += " "+user.getLastName();

				parameterStr += " token matched, deleting existing userPasswordTokens for: " + user.guid();
				clearResetTokens(user);
				user.setPasswordHash(PasswordHashing.hashPassword(userPasswordReset.getPassword(), passwordSalt));
				user.setPasswordUpdated(new Date());
				user.setPasswordUpdatedBy(userFullName);
				user.setExcessiveFailedLoginBlock(false);
				user.setFailedResetCount(0);
				userService.save(user);

        // UserPasswordHashAlgorithm added for VB migrated users where password hashes used algorithms set by DK.
        // When password is changed, the algorithm defaults back to lithium's default hashing algorithm, so if an entry exists,
        // it needs to be removed. Failure to remove will mean users are unable to authenticate after a password reset.
        userPasswordHashAlgorithmService.delete(user);

        parameterStr += " updating user ("+user.guid()+") with passwd details ";

				List<ChangeLogFieldChange> clfc = changeLogService.copy(user, new User(), new String[] { "passwordUpdatedBy", "excessiveFailedLoginBlock" });
				changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          user.getId(),
          user.guid(),
      util,
          messageSource.getMessage("UI_NETWORK_ADMIN.PASSWORDRESET.EXPLAIN2", null, locale),
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.EDIT_DETAILS,
          0,
          domainName
				);
				parameterStr += " changelog saved. ";
				parameterStr += " password reset completed. ";
				log.debug(parameterStr);

				validateCommsChannelAndSaveUser(user, userPasswordTokenType);
				passwordChangeNotificationService.sendSmsAndEmailNotification(user);
			} else {
				parameterStr += " UserPasswordToken mismatch :: "+userPasswordReset.getToken();
				parameterStr += ". Could not reset password for user (" + user.guid() + ")";

				user.incFailedResetCount();
				parameterStr += ". Attempt: " + user.getFailedResetCount() + ". ";
				userService.save(user);

				if (user.getFailedResetCount() == domainFailedResetCountSetting) {
					changeLogService.registerChangesForNotesWithFullNameAndDomain(
            "user",
            "comment",
            user.getId(),
            user.guid(),
            null,
            messageSource.getMessage("SERVICE_USER.PASSWORDRESET.493_BLOCKED_ATTEMPT", null, locale),
            null,
            null,
            Category.ACCOUNT,
            SubCategory.EDIT_DETAILS,
            0,
            domainName
					);
				}

				log.warn(parameterStr);
				throw new Status424InvalidResetTokenException(messageSource.getMessage("SERVICE_USER.PASSWORDRESET.INVALIDTOKEN", null, locale));
			}
		} catch (Status100InvalidInputDataException | Status424InvalidResetTokenException | Status493ExcessiveFailedPasswordResetBlockException e) {
			throw e;
		} catch (Exception e) {
			long ms = new Date().getTime();
			parameterStr += " exception: ("+ms+") :: " + ExceptionMessageUtil.allMessages(e);
			log.error(parameterStr, e);
			throw new Status999GeneralFailureException("An unexpected server error has occurred: "+ms);
		}
	}

	public void sendPasswordResetTokenEmail(String domainName, String emailOrUsername, boolean isNumeric)
			throws Exception {
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsername);
		List<UserPasswordToken> tokens = userPasswordTokenRepository.findByUser(user);
		if (!tokens.isEmpty())
			clearResetTokens(user);

		String token = isNumeric ? generateNumericEmailToken() : generateEmailToken();
		while (userPasswordTokenRepository.findByUserAndToken(user, token) != null) {
			token = isNumeric ? generateNumericEmailToken() : generateEmailToken();
		}

		UserPasswordToken userPasswordToken = new UserPasswordToken(token, user, new Date(), Type.EMAIL);
		userPasswordTokenRepository.save(userPasswordToken);

		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
    placeholders.add(USER_PASSWORD_RESET_URL.from(domain.getUrl() + "/forgotpassword?action=passwordreset&user="
        + emailOrUsername + "&token=" + userPasswordToken.getToken()));
    placeholders.add(USER_PASSWORD_RESET_TOKEN.from(isNumeric ? userPasswordToken.getBase64DecodedToken() : userPasswordToken.getToken()));
    placeholders.add(USER_PASSWORD_RESET_CODE.from(isNumeric ? userPasswordToken.getBase64DecodedToken() : userPasswordToken.getToken()));

    if (user.getEmail() != null) {
      mailStream.process(EmailData.builder()
          .authorSystem()
          .emailTemplateName(isNumeric ? "password.reset.numeric" : "password.reset")
          .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage()).to(user.getEmail()).priority(MAIL_PRIORITY_HIGH)
          .userGuid(user.guid()).placeholders(placeholders)
          .domainName(domainName)
          .build());
    }
	}

	public void clearResetTokens(User user) {
		userPasswordTokenRepository.deleteByUser(user);
	}
	public void clearResetTokensAndCount(String userGuid, LithiumTokenUtil tokenUtil) throws Exception {
		User user = userService.findFromGuid(userGuid);
		clearResetTokens(user);
		user.setFailedResetCount(0);
		userService.save(user);

		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
    Locale locale = LocaleContextHolder.getLocale();

		changeLogService.registerChangesForNotesWithFullNameAndDomain(
      "user",
      "edit",
      user.getId(),
      tokenUtil.guid(),
      tokenUtil,
      messageSource.getMessage("SERVICE_USER.PASSWORDRESET.CLEAR_FAILED_ATTEMPTS", null, locale),
      null,
      null,
      Category.ACCOUNT,
      SubCategory.EDIT_DETAILS,
      0,
      domain.getName()
		);
	}

	public void sendPasswordResetTokenSms(String domainName, String emailOrUsernameOrPhoneNumber) throws Exception {
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsernameOrPhoneNumber);
		List<UserPasswordToken> tokens = userPasswordTokenRepository.findByUser(user);
		if (!tokens.isEmpty())
			clearResetTokens(user);

		String token = generateMobileToken();
		while (userPasswordTokenRepository.findByUserAndToken(user, token) != null) {
			token = generateMobileToken();
		}

		UserPasswordToken userPasswordToken = new UserPasswordToken(token, user, new Date(), Type.SMS);
		userPasswordTokenRepository.save(userPasswordToken);

		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
		placeholders.add(USER_PASSWORD_RESET_URL.from(domain.getUrl() + "?action=passwordreset&user="
				+ emailOrUsernameOrPhoneNumber + "&token=" + userPasswordToken.getToken()));
		placeholders.add(USER_PASSWORD_RESET_TOKEN.from(userPasswordToken.getBase64DecodedToken()));

		if (user.getCellphoneNumber() != null) {
			smsStream.process(SMSBasic.builder().smsTemplateName("sms.password.reset").smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
					.to(user.getCellphoneNumber()).priority(MAIL_PRIORITY_HIGH)
					.userGuid(user.guid()).placeholders(placeholders)
					.domainName(domainName).build());
		}
	}

	public boolean validateTokenOnly(String domain, String emailOrUsername, String token) throws Exception {
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domain, emailOrUsername);
		UserPasswordToken validToken = findToken(user, token, true);
		return validToken != null && validToken.getBase64DecodedToken().equals(token);
	}

	@Transactional(rollbackFor = Exception.class)
  public boolean validateTokenAndResetPassword(String domain, String emailOrUsername, String token, String password)
			throws Exception {
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domain, emailOrUsername);
		UserPasswordToken validToken = findToken(user, token, false);
		if (validToken != null && validToken.getToken().equals(token)) {
			Type userPasswordTokenType = null;
			if (validToken.getType() != null)
				userPasswordTokenType = Type.fromType(validToken.getType().type());
			userPasswordTokenRepository.delete(validToken);
			user.setPasswordHash(PasswordHashing.hashPassword(password, passwordSalt));
			validateCommsChannelAndSaveUser(user, userPasswordTokenType);

      // UserPasswordHashAlgorithm added for VB migrated users where password hashes used algorithms set by DK.
      // When password is changed, the algorithm defaults back to lithium's default hashing algorithm, so if an entry exists,
      // it needs to be removed. Failure to remove will mean users are unable to authenticate after a password reset.
      userPasswordHashAlgorithmService.delete(user);

      return true;
		} else {
			log.warn("Could not reset password for user (" + user.getUsername() + ").  Token (" + token
					+ ") is invalid");
			return false;
		}
	}

	private UserPasswordToken findToken(User user, String token, boolean isBase64Encoded) throws Exception {
		UserPasswordToken userPasswordToken = null;
		List<UserPasswordToken> userPasswordTokens = userPasswordTokenRepository.findByUser(user);
		for (UserPasswordToken upt: userPasswordTokens) {
			String dbToken = (isBase64Encoded)? upt.getBase64DecodedToken(): upt.getToken();
			if (dbToken.contentEquals(token)) {
				userPasswordToken = upt;
				break;
			}
		}
		return userPasswordToken;
	}

	private User validateCommsChannelAndSaveUser(User user, Type type) {
		if (type != null) {
			switch (type) {
				case EMAIL:
					user.setEmailValidated(true);
					break;
				case SMS:
					user.setCellphoneValidated(true);
					break;
			}
		}
		return userService.save(user);
	}
}
