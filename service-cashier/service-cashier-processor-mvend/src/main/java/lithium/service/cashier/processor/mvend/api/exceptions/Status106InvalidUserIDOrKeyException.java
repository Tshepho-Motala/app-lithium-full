package lithium.service.cashier.processor.mvend.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status106InvalidUserIDOrKeyException extends NotRetryableErrorCodeException {
    public Status106InvalidUserIDOrKeyException() {
        super(106, "Invalid UserID/Password Provided", Status106InvalidUserIDOrKeyException.class.getCanonicalName());
    }
}
