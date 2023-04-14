package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status500UnhandledCasinoClientException extends ErrorCodeException {
    public static final int CODE = 500;
    public Status500UnhandledCasinoClientException(String message) {
        super(CODE, "Call to casino service failed: " + message, Status500UnhandledCasinoClientException.class.getCanonicalName());
    }
}
