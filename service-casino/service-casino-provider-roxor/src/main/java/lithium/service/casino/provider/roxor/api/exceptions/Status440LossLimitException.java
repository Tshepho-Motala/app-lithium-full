package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status440LossLimitException extends RoxorErrorCodeException {
    public Status440LossLimitException() {
        super(ErrorCategory.EC_440.category(), ErrorCategory.EC_440.displayMessage(), Status440LossLimitException.class.getCanonicalName());
    }

    public Status440LossLimitException(Object context) {
        super(ErrorCategory.EC_440.category(), ErrorCategory.EC_440.displayMessage(), context,
                Status440LossLimitException.class.getCanonicalName());
    }
}
