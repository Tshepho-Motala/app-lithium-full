package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status499GeneralOutputResultException extends NotRetryableErrorCodeException {
    public static final int CODE = 499;
    public Status499GeneralOutputResultException(String message) {
        super(CODE, message, Status499GeneralOutputResultException.class.getCanonicalName());
    }
}
