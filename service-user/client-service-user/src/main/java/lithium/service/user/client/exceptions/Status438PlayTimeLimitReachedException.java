package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status438PlayTimeLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 438;
    public Status438PlayTimeLimitReachedException(String message) {
        super(CODE, message, null, Status438PlayTimeLimitReachedException.class.getCanonicalName());
    }
}
