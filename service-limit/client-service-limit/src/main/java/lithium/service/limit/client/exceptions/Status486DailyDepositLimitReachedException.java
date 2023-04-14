package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status486DailyDepositLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 486;
    public Status486DailyDepositLimitReachedException(String message) {
        super(CODE, message, Status486DailyDepositLimitReachedException.class.getCanonicalName());
    }
}
