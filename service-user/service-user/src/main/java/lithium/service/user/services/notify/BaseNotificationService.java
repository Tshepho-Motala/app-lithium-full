package lithium.service.user.services.notify;

import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserPlaceholderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
/**
 * Class is responsible for sending notifications to the existing mobile and email when mobile number or email is changed.
 */
public abstract class BaseNotificationService {
    @Autowired protected MailStream mailStream;
    @Autowired protected SMSStream smsStream;
    @Autowired CachingDomainClientService cachingDomainClientService;
    @Autowired private UserPlaceholderService userPlaceholderService;

    protected static final int MAIL_PRIORITY_HIGH = 1;
    protected static final int MAIL_PRIORITY_LOW = 2;

    protected Set<Placeholder> resolvePlaceholders(final User user) throws LithiumServiceClientFactoryException {
      Domain domain;

      try {
        domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
      } catch (Status550ServiceDomainClientException e) {
        throw new LithiumServiceClientFactoryException(e.getMessage(), e);
      }

      Set<Placeholder> placeholders = userPlaceholderService.getPlaceholdersWithExternalData(user, domain);
      placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
      return placeholders;
    }

    protected void sendEmail(final String emailTemplate, final String email, final String userGuid, final String domainName, Set<Placeholder> placeholders) {
      mailStream.process(
          EmailData.builder()
              .authorSystem()
              .emailTemplateName(emailTemplate)
              .emailTemplateLang(LocaleContextHolder.getLocale().getLanguage())
              .to(email)
              .priority(MAIL_PRIORITY_HIGH)
              .userGuid(userGuid)
              .placeholders(placeholders)
              .domainName(domainName)
              .build()
      );
    }

    protected void sendSms(final String smsTemplate, final String cellphoneNumber, final String userGuid, final String domainName, Set<Placeholder> placeholders) {
        smsStream.process(
                SMSBasic.builder()
                        .smsTemplateName(smsTemplate)
                        .smsTemplateLang(LocaleContextHolder.getLocale().getLanguage())
                        .to(cellphoneNumber)
                        .priority(MAIL_PRIORITY_HIGH)
                        .userGuid(userGuid)
                        .placeholders(placeholders)
                        .domainName(domainName)
                        .build()
        );
    }

    protected boolean notNullOrEmpty(final String data) {
      return data != null && !data.trim().isEmpty();
    }
}
