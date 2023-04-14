package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status421InvalidFileDataException extends ErrorCodeException {
    public static final int CODE = 421;
    public Status421InvalidFileDataException(String message) {
        super(CODE, "Error trying to read file data: " + message, Status421InvalidFileDataException.class.getCanonicalName());
    }
}
