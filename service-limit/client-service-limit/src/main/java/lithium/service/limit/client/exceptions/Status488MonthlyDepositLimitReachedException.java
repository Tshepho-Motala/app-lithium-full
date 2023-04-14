package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status488MonthlyDepositLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 488;
    public Status488MonthlyDepositLimitReachedException(String message) {
        super(CODE, message, Status488MonthlyDepositLimitReachedException.class.getCanonicalName());
    }
}
