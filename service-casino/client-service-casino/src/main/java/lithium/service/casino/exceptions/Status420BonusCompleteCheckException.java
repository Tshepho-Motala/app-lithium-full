package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status420BonusCompleteCheckException extends ErrorCodeException {
    public static final int CODE = 420;
    public Status420BonusCompleteCheckException(String message) {
        super(CODE, "Bonus completion check failed: " + message, Status420BonusCompleteCheckException.class.getCanonicalName());
    }
}
