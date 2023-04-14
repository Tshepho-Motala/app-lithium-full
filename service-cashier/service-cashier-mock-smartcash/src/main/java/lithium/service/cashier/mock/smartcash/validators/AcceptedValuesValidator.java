package lithium.service.cashier.mock.smartcash.validators;

import lithium.service.cashier.mock.smartcash.validators.AcceptedValues;
import lithium.util.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AcceptedValuesValidator implements ConstraintValidator<AcceptedValues, String> {
    private AcceptedValues acceptedValues;
    @Override
    public void initialize(AcceptedValues acceptedValues) {
        this.acceptedValues = acceptedValues;
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext cxt) {
        if (StringUtil.isEmpty(fieldValue) || !Arrays.stream(acceptedValues.values()).anyMatch(v -> fieldValue.equalsIgnoreCase(v))) {
            cxt.disableDefaultConstraintViolation();
            cxt
                .buildConstraintViolationWithTemplate( " did not match")
                .addConstraintViolation();
            return false;
        }
        return true;
    }
}

