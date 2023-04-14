package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status901InvalidOrMissingParameters extends NotRetryableErrorCodeException {
    public Status901InvalidOrMissingParameters(String message) {
        super(901, message, Status901InvalidOrMissingParameters.class.getCanonicalName());
    }
}
