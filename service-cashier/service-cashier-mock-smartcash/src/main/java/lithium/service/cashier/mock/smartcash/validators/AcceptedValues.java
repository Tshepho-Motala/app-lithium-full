package lithium.service.cashier.mock.smartcash.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AcceptedValuesValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptedValues {
    String message() default "Unsupported value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public String[] values() default {};
}
