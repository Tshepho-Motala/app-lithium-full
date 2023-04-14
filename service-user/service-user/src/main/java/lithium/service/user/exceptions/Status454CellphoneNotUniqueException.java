package lithium.service.user.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status454CellphoneNotUniqueException extends NotRetryableErrorCodeException {
    public Status454CellphoneNotUniqueException(String message) {
        super(454, message, Status454CellphoneNotUniqueException.class.getCanonicalName());
    }
}
