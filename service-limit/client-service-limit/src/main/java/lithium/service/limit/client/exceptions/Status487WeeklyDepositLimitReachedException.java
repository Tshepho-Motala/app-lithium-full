package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status487WeeklyDepositLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 487;
    public Status487WeeklyDepositLimitReachedException(String message) {
        super(CODE, message, Status487WeeklyDepositLimitReachedException.class.getCanonicalName());
    }
}
