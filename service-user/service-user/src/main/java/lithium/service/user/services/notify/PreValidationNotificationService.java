package lithium.service.user.services.notify;

import java.util.Set;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * Class is responsible for sending notifications to the existing mobile and email when mobile number or email is changed.
 */
public class PreValidationNotificationService extends  BaseNotificationService {
    protected static final String EMAIL_EMAIL_CHANGE_TEMPLATE = "email.email.prechange";
    protected static final String SMS_EMAIL_CHANGE_TEMPLATE = "sms.email.prechange";
    protected static final String EMAIL_MOBILE_CHANGE_TEMPLATE = "email.mobile.prechange";
    protected static final String SMS_MOBILE_CHANGE_TEMPLATE = "sms.mobile.prechange";

    /**
     * Send a notification to oldEmail and oldCellphoneNumber if a
     * change is detected in either, when compared to the user object.
     * Available template configurations:
     * email.emailchanged
     * sms.emailchanged
     * email.mobilechanged
     * sms.mobilechanged
     * @param oldEmailAddress Email address prior to change
     * @param oldCellphoneNumber Cellphone number prior to change
     * @param user User object post modification
     */
    public void sendEmailOrCellphoneChangeNotification(final String oldEmailAddress, final String oldCellphoneNumber, final User user) {
        try {
            Set<Placeholder> placeholders = resolvePlaceholders(user);

            //Email change check and sending
            if (notNullOrEmpty(oldEmailAddress)
                && (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(oldEmailAddress))) {
                sendEmail(EMAIL_EMAIL_CHANGE_TEMPLATE, oldEmailAddress, user.guid(), user.domainName(), placeholders);
                if (notNullOrEmpty(oldCellphoneNumber)) {
                    sendSms(SMS_EMAIL_CHANGE_TEMPLATE, oldCellphoneNumber, user.guid(), user.domainName(), placeholders);
                }
            }

            //mobile change change check and sending
            if (notNullOrEmpty(oldCellphoneNumber)
                && (user.getCellphoneNumber() == null || !user.getCellphoneNumber().equalsIgnoreCase(oldCellphoneNumber))) {
                if (notNullOrEmpty(oldEmailAddress)) {
                    sendEmail(EMAIL_MOBILE_CHANGE_TEMPLATE, oldEmailAddress, user.guid(), user.domainName(), placeholders);
                }
                sendSms(SMS_MOBILE_CHANGE_TEMPLATE, oldCellphoneNumber, user.guid(), user.domainName(), placeholders);
            }
        } catch (Exception ex) {
            log.warn("Unable to perform pre-validation notification on user: " + user.guid(), ex);
        }
    }
}
