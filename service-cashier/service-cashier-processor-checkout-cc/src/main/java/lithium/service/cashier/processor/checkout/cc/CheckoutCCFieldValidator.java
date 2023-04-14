package lithium.service.cashier.processor.checkout.cc;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.validator.FieldValidator;
import lombok.Getter;

import java.util.regex.Pattern;

public class CheckoutCCFieldValidator extends FieldValidator {

    public static boolean validateCVV(final String key, final int stage, DoProcessorRequest request, DoProcessorResponse response) {
        return validate(CheckoutValidationType.CVV_LENGTH, key, stage, request, response);
    }

    public enum CheckoutValidationType implements FieldValidator.IValidationType {
        CVV_LENGTH(Pattern.compile("^[0-9]{3,4}$"), " is not 3-4 digits.");

        CheckoutValidationType(Pattern regex, String error) {
            this.regex = regex;
            this.error = error;
        }

        @Getter
        private Pattern regex;

        @Getter
        private String error;
    }
}
