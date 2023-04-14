package lithium.service.casino.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status418FailedToAllocateFreeMoneyException extends ErrorCodeException {
    public static final int CODE = 418;
    public Status418FailedToAllocateFreeMoneyException(String message) {
        super(CODE, "Failed to allocate free money: " + message, Status418FailedToAllocateFreeMoneyException.class.getCanonicalName());
    }
}
