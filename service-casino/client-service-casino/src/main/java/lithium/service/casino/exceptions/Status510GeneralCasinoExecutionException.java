package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status510GeneralCasinoExecutionException extends ErrorCodeException {
    public static final int CODE = 510;
    public Status510GeneralCasinoExecutionException(String message) {
        super(CODE, "Casino service method execution had an error: " + message, Status510GeneralCasinoExecutionException.class.getCanonicalName());
    }
}
