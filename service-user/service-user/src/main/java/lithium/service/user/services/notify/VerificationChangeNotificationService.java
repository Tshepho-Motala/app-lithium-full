package lithium.service.user.services.notify;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_VERIFICATION_STATUS;

import java.util.Optional;
import java.util.Set;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerificationChangeNotificationService extends BaseNotificationService {

    protected static final String VERIFICATION_STATUS_PREFIX = "verification.status.";
    protected static final String VERIFICATION_STATUS_CHANGE_TEMPLATE = "verification.status.change";
    protected static final String SMS_VERIFICATION_STATUS_CHANGE_TEMPLATE = "sms.verification.status.change";

    @Autowired private LimitInternalSystemService limitInternalSystemService;

    @Override
    protected Set<Placeholder> resolvePlaceholders(User user) throws LithiumServiceClientFactoryException {
      Set<Placeholder> placeholders = super.resolvePlaceholders(user);

      if (user.getVerificationStatus() != null) {
        try {
          String verificationStatusCode = limitInternalSystemService.getVerificationStatusCode(user.getVerificationStatus());
          placeholders.add(USER_VERIFICATION_STATUS.from(verificationStatusCode));
        } catch (Status500LimitInternalSystemClientException e) {
          log.error("Unable to add verification status on user: " + user, e);
        }
      } else {
        placeholders.add(USER_VERIFICATION_STATUS.from(Optional.empty()));
      }

      return placeholders;
    }

    /**
     * Send a notification email and sms when a player has successfully changed their password/pin
     * Available template configurations:
     * email.password.change.success
     * sms.password.change.success
     * @param user User object post modification
     */
    public void sendSmsAndEmailNotification(final User user) {
        sendSmsAndEmailNotification(user, true, true);
    }

    /**
     * Send a notification email and sms when a player has successfully changed their password/pin
     * Available template configurations:
     * email.password.change.success
     * sms.password.change.success
     * @param user User object post modification
     * @param sendSms Boolean object post modification
     */
    public void sendSmsAndEmailNotification(final User user, Boolean sendSms) {
        sendSmsAndEmailNotification(user, sendSms, true);
    }

    /**
     * Send a notification email and sms when a player has successfully changed their password/pin
     * Available template configurations:
     * email.password.change.success
     * sms.password.change.success
     * @param user User object post modification
     * @param sendSms Boolean object post modification
     * @param sendEmail Boolean object post modification
     */
    public void sendSmsAndEmailNotification(final User user, Boolean sendSms, Boolean sendEmail) {
        try {
            Set<Placeholder> placeholders = resolvePlaceholders(user);
            if (sendEmail) sendEmailNotification(user, placeholders);
            if (sendSms) sendSmsNotification(user, placeholders);
        } catch (Exception ex) {
            log.warn("Unable to perform password change success notification on user: " + user.guid(), ex);
        }
    }

    protected void sendSmsNotification(final User user, Set<Placeholder> placeholders) {
        if (notNullOrEmpty(user.getCellphoneNumber())) {
            sendSms(SMS_VERIFICATION_STATUS_CHANGE_TEMPLATE, user.getCellphoneNumber(), user.guid(), user.domainName(), placeholders);
        }
    }

    protected void sendEmailNotification(final User user, Set<Placeholder> placeholders) {
        if (notNullOrEmpty(user.getEmail())) {
          sendStaticTemplate(user, placeholders);
          sendDynamicTemplate(user, placeholders);
        }
    }

  private void sendDynamicTemplate(User user, Set<Placeholder> placeholders) {
    String templateName = buildTemplateName(user.getVerificationStatus());
    sendEmail(templateName, user.getEmail(), user.guid(), user.domainName(), placeholders);
  }

  private String buildTemplateName(long verificationStatusId) {
    try {
      String statusName = limitInternalSystemService.getVerificationStatusCode(verificationStatusId).trim().replaceAll(" ","_");
      return VERIFICATION_STATUS_PREFIX+statusName.toLowerCase();
    } catch (Status500LimitInternalSystemClientException e) {
      log.error("Cant get verification status by Id=" + verificationStatusId);
    }
    return "";
  }

  private void sendStaticTemplate(User user, Set<Placeholder> placeholders) {
    sendEmail(VERIFICATION_STATUS_CHANGE_TEMPLATE, user.getEmail(), user.guid(), user.domainName(), placeholders);
  }
}
