package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status443TimeLimitException extends RoxorErrorCodeException {
    public Status443TimeLimitException() {
        super(ErrorCategory.EC_443.category(), ErrorCategory.EC_443.displayMessage(), Status443TimeLimitException.class.getCanonicalName());
    }

    public Status443TimeLimitException(Object context) {
        super(ErrorCategory.EC_443.category(), ErrorCategory.EC_443.displayMessage(), context,
                Status443TimeLimitException.class.getCanonicalName());
    }
}
