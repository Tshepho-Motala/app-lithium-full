package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status405BonusDeleteNotAllowedException extends NotRetryableErrorCodeException {
    public static final int CODE = 405;
    public Status405BonusDeleteNotAllowedException(String message) {
        super(CODE, message, Status405BonusDeleteNotAllowedException.class.getCanonicalName());
    }
}
