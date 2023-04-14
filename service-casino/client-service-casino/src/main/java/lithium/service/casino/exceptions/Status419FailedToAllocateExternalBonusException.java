package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status419FailedToAllocateExternalBonusException extends ErrorCodeException {
    public static final int CODE = 419;
    public Status419FailedToAllocateExternalBonusException(String message) {
        super(CODE, "Failed to allocate external bonus: " + message, Status419FailedToAllocateExternalBonusException.class.getCanonicalName());
    }
}
