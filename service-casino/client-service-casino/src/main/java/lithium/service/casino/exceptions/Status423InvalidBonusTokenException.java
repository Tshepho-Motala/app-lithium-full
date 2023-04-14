package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status423InvalidBonusTokenException extends ErrorCodeException {
    public static final int CODE = 423;
    public Status423InvalidBonusTokenException(String message) {
        super(CODE, "No valid bonus token was found: " + message, Status423InvalidBonusTokenException.class.getCanonicalName());
    }
}
