package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status480PendingDepositLimitException extends NotRetryableErrorCodeException {
    public static final int CODE = 480;
    public Status480PendingDepositLimitException(String message) {
        super(CODE, message, null, Status480PendingDepositLimitException.class.getCanonicalName());
    }
}
