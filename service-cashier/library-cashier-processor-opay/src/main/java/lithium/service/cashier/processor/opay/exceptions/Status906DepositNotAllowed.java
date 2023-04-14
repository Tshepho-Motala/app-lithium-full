package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status906DepositNotAllowed extends NotRetryableErrorCodeException {
    public Status906DepositNotAllowed(String message) {
        super(906, message, Status906DepositNotAllowed.class.getCanonicalName());
    }
}
