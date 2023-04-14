package lithium.service.promo.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411InvalidUserCreateException extends NotRetryableErrorCodeException {
    public static final int CODE = 411;
    public Status411InvalidUserCreateException() {
        super(CODE, "", Status411InvalidUserCreateException.class.getCanonicalName());
    }
}
