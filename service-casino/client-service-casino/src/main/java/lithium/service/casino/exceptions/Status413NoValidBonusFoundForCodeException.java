package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status413NoValidBonusFoundForCodeException extends ErrorCodeException {
    public static final int CODE = 413;
    public Status413NoValidBonusFoundForCodeException(String bonusCode) {
        super(CODE, "No valid bonus was found for bonus code: " + bonusCode, Status413NoValidBonusFoundForCodeException.class.getCanonicalName());
    }
}
