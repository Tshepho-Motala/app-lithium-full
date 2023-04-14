package lithium.service.translate.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status422InvalidLanguageException extends ErrorCodeException {
    public static final int CODE = 422;

    public Status422InvalidLanguageException(String message) {
        super(CODE, message, Status422InvalidLanguageException.class.getCanonicalName());
    }

}
