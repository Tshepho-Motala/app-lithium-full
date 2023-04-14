package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status470HashInvalidException extends NotRetryableErrorCodeException {
    public static final int CODE = 470;
    public Status470HashInvalidException() {
        super(CODE, "Invalid sha256 hash", Status470HashInvalidException.class.getCanonicalName());
    }
}
