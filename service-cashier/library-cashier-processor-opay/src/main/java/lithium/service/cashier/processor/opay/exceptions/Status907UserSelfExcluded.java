package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status907UserSelfExcluded extends NotRetryableErrorCodeException {
    public Status907UserSelfExcluded(String message) {
        super(907, message, Status907UserSelfExcluded.class.getCanonicalName());
    }
}
