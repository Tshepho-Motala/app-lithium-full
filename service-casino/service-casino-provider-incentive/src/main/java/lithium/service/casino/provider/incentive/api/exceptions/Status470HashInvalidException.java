package lithium.service.casino.provider.incentive.api.exceptions;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;

public class Status470HashInvalidException extends NotRetryableErrorCodeException {
    public Status470HashInvalidException() {
        super(470, "Invalid sha256 hash", Status470HashInvalidException.class.getCanonicalName());
    }
}
