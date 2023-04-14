package lithium.service.translate.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status421InvalidDomainException extends ErrorCodeException {
    public static final int CODE = 421;

    public Status421InvalidDomainException(String message) {
        super(CODE, message, Status421InvalidDomainException.class.getCanonicalName());
    }

}
