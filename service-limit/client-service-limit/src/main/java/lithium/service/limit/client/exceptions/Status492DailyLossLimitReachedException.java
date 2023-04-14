package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status492DailyLossLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 492;
    public Status492DailyLossLimitReachedException(String message) {
        super(CODE, message, Status492DailyLossLimitReachedException.class.getCanonicalName());
    }
}
