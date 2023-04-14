package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status422InvalidGrantBonusException  extends NotRetryableErrorCodeException {
    public Status422InvalidGrantBonusException(int code, String message) {
        super(code, message, Status422InvalidGrantBonusException.class.getCanonicalName());
    }
}
