package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status417BonusIsNotValidForPlayerException extends ErrorCodeException {
    public static final int CODE = 417;
    public Status417BonusIsNotValidForPlayerException(String message) {
        super(CODE, "Bonus is not valid for player: " + message, Status417BonusIsNotValidForPlayerException.class.getCanonicalName());
    }
}
