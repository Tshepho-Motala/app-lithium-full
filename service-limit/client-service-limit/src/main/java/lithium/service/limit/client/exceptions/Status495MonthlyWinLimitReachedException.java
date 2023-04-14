package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status495MonthlyWinLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 495;
    public Status495MonthlyWinLimitReachedException(String message) {
        super(CODE, message, Status495MonthlyWinLimitReachedException.class.getCanonicalName());
    }
}
