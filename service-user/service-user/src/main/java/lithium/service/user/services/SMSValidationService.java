package lithium.service.user.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response.Status;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.user.data.entities.MobileValidationToken;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.MobileValidationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_SMS_VALIDATE_TOKEN;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_SMS_VALIDATE_URL;

@Service
@Slf4j
public class SMSValidationService extends UserValidationBaseService {
	@Autowired MobileValidationTokenRepository mobileValidationTokenRepository;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired private CachingDomainClientService cachingDomainClientService;
	public void sendCellphoneValidationTokenSms(String domainName, String emailOrUsernameOrPhoneNumber, Boolean cellnumberChanged, Boolean resend) throws Exception {
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsernameOrPhoneNumber);
		MobileValidationToken token = mobileValidationTokenRepository.findByUser(user);
		if (token != null) mobileValidationTokenRepository.delete(token);
		token = new MobileValidationToken(generateMobileToken(), user, new Date());
		mobileValidationTokenRepository.save(token);
		
		String smsTemplate = "";
		if (resend) {
			smsTemplate = "sms.validation.resend";
		} else if (cellnumberChanged) {
			smsTemplate = "sms.validation.cellnumber.changed";
		} else {
			smsTemplate = "sms.validation";
		}
		
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
		placeholders.add(USER_SMS_VALIDATE_URL.from(domain.getUrl() + "?action=smsvalidation" + (resend? "&resend=" + resend.toString() : "") + "&user=" + emailOrUsernameOrPhoneNumber + "&token=" + token.getToken()));
		placeholders.add(USER_SMS_VALIDATE_TOKEN.from(token.getToken()));
		
		smsStream.process(
			SMSBasic.builder()
				.smsTemplateName(smsTemplate)
				.smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
				.to(user.getCellphoneNumber())
				.priority(MAIL_PRIORITY_HIGH)
				.userGuid(user.guid())
				.placeholders(placeholders)
				.domainName(domainName)
				.build()
		);
	}
	
	public Status validateTokenAndSetCellphoneValidated(String domainName, String emailOrUsernameOrPhoneNumber, Boolean resend, String token) throws Exception {
		log.info("Cellphone validation request: cellphone (" + emailOrUsernameOrPhoneNumber + ") token (" + token + ")");
		User user = findUserByDomainAndEmailOrUsernameOrPhoneNumber(domainName, emailOrUsernameOrPhoneNumber);
		if (user.isCellphoneValidated()) {
			log.warn("Cellphone number (" + user.getCellphoneNumber() + ") for user (" + user.getUsername() + ") is already validated.");
			return Status.CONFLICT;
		}
		MobileValidationToken validToken = mobileValidationTokenRepository.findByUser(user);
		if (validToken != null && validToken.getToken().equals(token)) {
			mobileValidationTokenRepository.delete(validToken);
			if (!user.isWelcomeSmsSent()) {
				log.info("Sending welcome sms to (" + emailOrUsernameOrPhoneNumber + ")");
				Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);
				
				smsStream.process(
					SMSBasic.builder()
						.smsTemplateName("sms.welcome")
            .smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
						.to(user.getCellphoneNumber())
						.priority(MAIL_PRIORITY_HIGH)
						.userGuid(user.guid())
						.placeholders(placeholders)
						.domainName(domainName)
						.build()
				);
				//user.setWelcomeSmsSent(true);
        user = userService.saveEmailWelcomeStatus(user.getId(), true);
			}
			
			//user.setCellphoneValidated(true);
			//userService.save(user);
			userService.saveCellphoneValidated(user.getId(), true);
      List<ChangeLogFieldChange> clfc = changeLogService.copy(
          user,
          new User(),
          new String[]{
              "cellphoneValidated"
          }
      );
      changeLogService.registerChangesWithDomain("user", "edit", user.getId(), user.guid(),
          "Cellphone number has been validated", null , clfc,
          Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domainName);
			return Status.OK;
		} else {
			log.warn("Could not validate cellphone number (" + user.getCellphoneNumber() + ") for user (" + user.getUsername() + "). Token (" + token + ") is invalid");
			return Status.INVALID_DATA;
		}
	}
}
