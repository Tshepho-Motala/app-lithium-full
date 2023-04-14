package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status412InvalidCustomFreeMoneyAmountException extends ErrorCodeException {
    public static final int CODE = 412;
    public Status412InvalidCustomFreeMoneyAmountException(String amount) {
        super(CODE, "An invalid custom free money amount was supplied: " + amount, Status412InvalidCustomFreeMoneyAmountException.class.getCanonicalName());
    }
}
