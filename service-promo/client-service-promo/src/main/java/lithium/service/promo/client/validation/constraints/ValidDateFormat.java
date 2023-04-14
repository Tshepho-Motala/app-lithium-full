package lithium.service.promo.client.validation.constraints;

import lithium.service.promo.client.validation.validators.DateFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "An invalid date was provided, date must in the format yyyy-MM-dd HH:mm:ss (e.g. 2023-01-10 13:30:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
