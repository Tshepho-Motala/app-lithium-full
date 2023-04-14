package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404BonusNotFoundException extends NotRetryableErrorCodeException {
    public static final int CODE = 404;
    public Status404BonusNotFoundException(String message) {
        super(CODE, message, Status404BonusNotFoundException.class.getCanonicalName());
    }
}
