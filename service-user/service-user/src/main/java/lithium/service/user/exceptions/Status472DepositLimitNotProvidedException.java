package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status472DepositLimitNotProvidedException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 472;
    public Status472DepositLimitNotProvidedException(String message) {
        super(ERROR_CODE, message, Status472DepositLimitNotProvidedException.class.getCanonicalName());
    }
}
