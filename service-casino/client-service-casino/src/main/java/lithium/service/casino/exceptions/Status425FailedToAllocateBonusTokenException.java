package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status425FailedToAllocateBonusTokenException extends ErrorCodeException {
    public static final int CODE = 425;
    public Status425FailedToAllocateBonusTokenException(String message) {
        super(CODE, "Failed to allocate bonus token to player: " + message, Status425FailedToAllocateBonusTokenException.class.getCanonicalName());
    }
}
