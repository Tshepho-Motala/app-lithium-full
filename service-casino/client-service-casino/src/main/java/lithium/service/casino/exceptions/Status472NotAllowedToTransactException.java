package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status472NotAllowedToTransactException extends NotRetryableErrorCodeException {
    public static final int CODE = 472;
    public Status472NotAllowedToTransactException(String message) {
        super(CODE, message, Status472NotAllowedToTransactException.class.getCanonicalName());
    }
}
