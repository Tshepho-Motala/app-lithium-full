package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status494DailyWinLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 494;
    public Status494DailyWinLimitReachedException(String message) {
        super(CODE, message, Status494DailyWinLimitReachedException.class.getCanonicalName());
    }
}
