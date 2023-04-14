package lithium.service.user.services.notify;

import java.util.HashSet;
import java.util.Set;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * Class is responsible for sending notifications users when they block their accounts due to excessive incorrect logins
 */
public class FailedLoginBlockNotificationService {
    @Autowired protected MailStream mailStream;
    @Autowired protected SMSStream smsStream;
    @Autowired private CachingDomainClientService cachingDomainClientService;

    protected static final int MAIL_PRIORITY_HIGH = 1;
    protected static final int MAIL_PRIORITY_LOW = 2;

    protected static final String EMAIL_FAILED_LOGIN_BLOCK_TEMPLATE = "email.failed.login.block";
    protected static final String SMS_FAILED_LOGIN_BLOCK_TEMPLATE = "sms.failed.login.block";

    /**
     * Send a notification to oldEmail and oldCellphoneNumber if a
     * change is detected in either, when compared to the user object.
     * Available template configurations:
     * email.emailchanged
     * sms.emailchanged
     * email.mobilechanged
     * sms.mobilechanged
     * @param user User object post block
     */
    public void sendEmailAndCellphoneUserBlockNotification(final User user) {
        try {
            log.debug("Start sendEmailAndCellphoneUserBlockNotification: " + user);
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
            Set<Placeholder> placeholders = new HashSet<>();
            placeholders.addAll(new UserToPlaceholderBinder(user).completePlaceholders());
            placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());

            //Email change check and sending
            if (user.getEmail() != null) {
                sendEmail(EMAIL_FAILED_LOGIN_BLOCK_TEMPLATE, user.getEmail(), user.guid(), user.getDomain().getName(), placeholders);
                log.debug("Email sent in sendEmailAndCellphoneUserBlockNotification: " + user);
            }
            if (user.getCellphoneNumber() != null) {
                sendSms(SMS_FAILED_LOGIN_BLOCK_TEMPLATE, user.getCellphoneNumber(), user.guid(), user.getDomain().getName(), placeholders);
                log.debug("Sms sent in sendEmailAndCellphoneUserBlockNotification: " + user);
            }
        } catch (Exception ex) {
            log.warn("Unable to perform user excessive login block failure notification on user: " + user.guid(), ex);
        }
        log.debug("Complete sendEmailAndCellphoneUserBlockNotification: " + user);
    }

    private void sendEmail(final String emailTemplate, final String email, final String userGuid, final String domainName, Set<Placeholder> placeholders) {

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

    private void sendSms(final String smsTemplate, final String cellphoneNumber, final String userGuid, final String domainName, Set<Placeholder> placeholders) {

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

}
