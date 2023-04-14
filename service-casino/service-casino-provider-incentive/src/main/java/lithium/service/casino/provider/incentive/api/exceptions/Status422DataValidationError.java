package lithium.service.casino.provider.incentive.api.exceptions;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;

public class Status422DataValidationError extends NotRetryableErrorCodeException {
    public Status422DataValidationError(String message) {
        super(422, "Data validation failed: " + message, Status422DataValidationError.class.getCanonicalName());
    }
}
