package lithium.service.reward.validation.constraints;

import lithium.service.client.objects.Granularity;
import lithium.service.reward.validation.validators.GranularityValidator;
import lithium.service.user.client.validators.safetext.SafeTextValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = GranularityValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGranularity {
    Granularity[] allowed();
    String message() default "An Invalid value was provided for granularity";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
