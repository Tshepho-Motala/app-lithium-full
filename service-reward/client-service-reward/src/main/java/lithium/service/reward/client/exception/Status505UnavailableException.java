package lithium.service.reward.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status505UnavailableException extends NotRetryableErrorCodeException {
    public static final int CODE = 505;
    public Status505UnavailableException(String message) {
        super(CODE, message, Status505UnavailableException.class.getCanonicalName());
    }
}
