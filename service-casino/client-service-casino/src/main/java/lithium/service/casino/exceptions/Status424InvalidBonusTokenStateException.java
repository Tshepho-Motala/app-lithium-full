package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status424InvalidBonusTokenStateException extends ErrorCodeException {
    public static final int CODE = 424;
    public Status424InvalidBonusTokenStateException(String message) {
        super(CODE, "The bonus token is in an invalid state: " + message, Status424InvalidBonusTokenStateException.class.getCanonicalName());
    }
}
