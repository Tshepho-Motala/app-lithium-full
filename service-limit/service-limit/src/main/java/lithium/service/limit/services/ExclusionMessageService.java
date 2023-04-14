package lithium.service.limit.services;

import lithium.service.limit.client.objects.PlayerExclusionV2;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class ExclusionMessageService {
    @Autowired @Setter
    MessageSource messageSource;

    public void populateMessage(PlayerExclusionV2 exclusion, Locale locale) {

        if (exclusion == null) return;

        String message = getMessage(exclusion.getExpiryDate(), locale);
        exclusion.setMessage(message);

    }

    private String getMessage(Date expirationDate, Locale locale) {
        if (expirationDate == null) {
            return messageSource.getMessage("SERVICE-LIMIT.SELF_EXCLUSION_PERMANENT", null, locale);
        }

        String dateFormat = "dd-MM-yyyy HH:mm";

        if (expirationDate.after(new Date())) {
            return messageSource.getMessage("SERVICE-LIMIT.SELF_EXCLUSION_DATE", new Object[]{new SimpleDateFormat(dateFormat).format(
                expirationDate
            )}, locale);
        } else {
            return messageSource.getMessage("SERVICE-LIMIT.SELF_EXCLUSION_EXPIRED_CONTACT_CS", null, locale);
        }
    }

    private String getMessage(Locale locale) {
        return messageSource.getMessage("SERVICE-LIMIT.SELF_EXCLUSION_NO_DATE", null, locale);
    }

}
