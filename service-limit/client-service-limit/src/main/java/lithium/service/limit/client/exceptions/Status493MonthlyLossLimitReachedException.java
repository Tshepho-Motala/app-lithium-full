package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status493MonthlyLossLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 493;
    public Status493MonthlyLossLimitReachedException(String message) {
        super(CODE, message, Status493MonthlyLossLimitReachedException.class.getCanonicalName());
    }
}
