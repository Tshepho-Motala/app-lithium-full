package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status414NoValidBonusRevisionFoundException extends ErrorCodeException {
    public static final int CODE = 414;
    public Status414NoValidBonusRevisionFoundException(String message) {
        super(CODE, "No valid bonus revision was found: " + message, Status414NoValidBonusRevisionFoundException.class.getCanonicalName());
    }
}
