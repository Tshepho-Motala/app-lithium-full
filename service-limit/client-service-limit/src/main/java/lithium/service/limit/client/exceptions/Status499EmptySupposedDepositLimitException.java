package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status499EmptySupposedDepositLimitException extends NotRetryableErrorCodeException {
    public static final int CODE = 499;
    public Status499EmptySupposedDepositLimitException(String message) {
        super(CODE, message, null, Status499EmptySupposedDepositLimitException.class.getCanonicalName());
    }
}
