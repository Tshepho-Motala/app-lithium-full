package lithium.service.user.services.notify;

import java.util.Set;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * Class is responsible for sending notifications to the existing mobile and email when mobile number or email is changed.
 */
public class PasswordChangeNotificationService extends BaseNotificationService {
    protected static final String EMAIL_PASSWORD_CHANGE_SUCCESS_TEMPLATE = "email.password.change.success";
    protected static final String SMS_PASSWORD_CHANGE_SUCCESS_TEMPLATE = "sms.password.change.success";

    /**
     * Send a notification email and sms when a player has successfully changed their password/pin
     * Available template configurations:
     * email.password.change.success
     * sms.password.change.success
     * @param user User object post modification
     */
    public void sendSmsAndEmailNotification(final User user) {
        try {
          Set<Placeholder> placeholders = resolvePlaceholders(user);
            sendEmailNotification(user, placeholders);
            sendSmsNotification(user, placeholders);
        } catch (Exception ex) {
            log.warn("Unable to perform password change success notification on user: " + user.guid(), ex);
        }
    }

    protected void sendSmsNotification(final User user, Set<Placeholder> placeholders) {
        if (notNullOrEmpty(user.getCellphoneNumber())) {
            sendSms(SMS_PASSWORD_CHANGE_SUCCESS_TEMPLATE, user.getCellphoneNumber(), user.guid(), user.domainName(), placeholders);
        }
    }

    /**
     * Send an sms notification when a player has successfully changed their password/pin
     * Available template configurations:
     * sms.password.change.success
     * @param user User object post modification
     */
    public void sendSmsNotification(final User user) {
        try {
            sendSmsNotification(user, resolvePlaceholders(user));
        } catch (LithiumServiceClientFactoryException e) {
            log.warn("Unable to perform password change success notification on user using sms: " + user.guid(), e);
        }
    }

    protected void sendEmailNotification(final User user, Set<Placeholder> placeholders) {
        if (notNullOrEmpty(user.getEmail())) {
            sendEmail(EMAIL_PASSWORD_CHANGE_SUCCESS_TEMPLATE, user.getEmail(), user.guid(), user.domainName(), placeholders);
        }
    }

    /**
     * Send an email notification when a player has successfully changed their password/pin
     * Available template configurations:
     * email.password.change.success
     * @param user User object post modification
     */
    public void sendEmailNotification(final User user) {
        try {
            sendEmailNotification(user, resolvePlaceholders(user));
        } catch (LithiumServiceClientFactoryException e) {
            log.warn("Unable to perform password change success notification on user using email: " + user.guid(), e);
        }
    }
}
