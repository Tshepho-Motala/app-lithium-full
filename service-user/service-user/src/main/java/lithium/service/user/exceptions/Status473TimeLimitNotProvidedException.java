package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status473TimeLimitNotProvidedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 473;
    public Status473TimeLimitNotProvidedException(String message) {
        super(ERROR_CODE, message, Status473TimeLimitNotProvidedException.class.getCanonicalName());
    }
}
