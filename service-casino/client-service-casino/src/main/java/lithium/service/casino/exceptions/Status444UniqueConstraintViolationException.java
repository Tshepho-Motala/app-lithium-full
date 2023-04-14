package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status444UniqueConstraintViolationException extends ErrorCodeException {
    public static final int CODE = 444;
    public Status444UniqueConstraintViolationException(String nameAndValueOfParameter) {
        super(CODE, "Unique constraints violated: " + nameAndValueOfParameter, Status444UniqueConstraintViolationException.class.getCanonicalName());
    }
}
