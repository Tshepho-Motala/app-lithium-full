package lithium.service.user.services;

import java.util.Set;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.sms.client.objects.SMSBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.user.data.entities.User;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class UserStatusService extends UserValidationBaseService {
  @Autowired private CachingDomainClientService cachingDomainClientService;

	public void sendAccountEnabledEmail(User user) throws Exception {
		Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
    Set<Placeholder> placeholders = constructBasicPlaceholders(domain, user);

    if (user.getEmail() != null) {
      mailStream.process(
          EmailData.builder()
              .authorSystem()
              .emailTemplateName("user.account.enabled")
              .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
              .to(user.getEmail())
              .priority(MAIL_PRIORITY_HIGH)
              .userGuid(user.guid())
              .placeholders(placeholders)
              .domainName(user.getDomain().getName())
              .build()
      );
    }

    if (user.getCellphoneNumber() != null) {
      smsStream.process(SMSBasic.builder()
          .domainName(user.domainName())
          .smsTemplateName("sms.user.account.enabled")
          .smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
          .to(user.getCellphoneNumber())
          .userGuid(user.guid())
          .priority(MAIL_PRIORITY_HIGH)
          .placeholders(placeholders)
          .build()
      );
    }
	}
}
