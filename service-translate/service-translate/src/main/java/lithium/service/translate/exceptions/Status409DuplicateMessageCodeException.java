package lithium.service.translate.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status409DuplicateMessageCodeException extends NotRetryableErrorCodeException {
    public static final int CODE = 409;
    public Status409DuplicateMessageCodeException(String message) {
        super(CODE, message, Status409DuplicateMessageCodeException.class.getCanonicalName());
    }
}
