package lithium.service.cashier.processor.opay.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status908OpayServiceError extends NotRetryableErrorCodeException {
    public Status908OpayServiceError(String message) {
        super(908, message, Status908OpayServiceError.class.getCanonicalName());
    }
}
