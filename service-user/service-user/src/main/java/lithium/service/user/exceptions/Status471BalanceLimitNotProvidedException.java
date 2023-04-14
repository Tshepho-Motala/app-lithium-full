package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status471BalanceLimitNotProvidedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 471;
    public Status471BalanceLimitNotProvidedException(String message) {
        super(ERROR_CODE, message, Status471BalanceLimitNotProvidedException.class.getCanonicalName());
    }
}
