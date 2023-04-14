package lithium.service.promo.client.validation.validators;

import lithium.service.promo.client.validation.constraints.ValidDateFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String>
{
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean validaFormat;

        try {
            dateTimeFormatter.parse(s);
            validaFormat = true;
        }
        catch (Exception e) {
            validaFormat = false;
        }
        return validaFormat;
    }
}
