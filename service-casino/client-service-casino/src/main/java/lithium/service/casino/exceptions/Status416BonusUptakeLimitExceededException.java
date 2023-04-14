package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status416BonusUptakeLimitExceededException extends ErrorCodeException {
    public static final int CODE = 416;
    public Status416BonusUptakeLimitExceededException(String uptakeLimit) {

        super(CODE, "Bonus uptake limit exceeded: " + uptakeLimit, Status416BonusUptakeLimitExceededException.class.getCanonicalName());
    }
}
