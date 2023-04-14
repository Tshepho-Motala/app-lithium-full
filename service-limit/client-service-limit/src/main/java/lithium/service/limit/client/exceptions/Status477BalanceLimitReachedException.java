package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status477BalanceLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 477;
    public Status477BalanceLimitReachedException(String message) {
        super(CODE, message, null, Status477BalanceLimitReachedException.class.getCanonicalName());
    }
}
