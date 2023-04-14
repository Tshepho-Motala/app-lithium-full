package lithium.service.reward.validation.constraints;

import lithium.service.reward.validation.validators.NotificationMessageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotificationMessageValidator.class)
public @interface ValidRewardNotificationMessage {
    String message() default "notificationMessage is required for non-instant rewards";

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
