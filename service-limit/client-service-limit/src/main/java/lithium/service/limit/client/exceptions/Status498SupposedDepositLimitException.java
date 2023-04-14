package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status498SupposedDepositLimitException extends NotRetryableErrorCodeException {
    public static final int CODE = 498;
    public Status498SupposedDepositLimitException(String message) {
        super(CODE, message, null, Status498SupposedDepositLimitException.class.getCanonicalName());
    }
}
