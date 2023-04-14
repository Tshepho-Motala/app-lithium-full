package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status481DomainDepositLimitDisabledException extends NotRetryableErrorCodeException {
    public static final int CODE = 481;
    public Status481DomainDepositLimitDisabledException(String message) {
        super(CODE, message, null, Status481DomainDepositLimitDisabledException.class.getCanonicalName());
    }
}
