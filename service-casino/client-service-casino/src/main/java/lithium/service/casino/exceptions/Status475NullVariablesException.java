package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status475NullVariablesException extends NotRetryableErrorCodeException {
    public Status475NullVariablesException(String message) {
        super(475, message, Status475NullVariablesException.class.getCanonicalName());
    }
}
