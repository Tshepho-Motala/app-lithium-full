package lithium.exceptions;

public class Status449AccountFrozenCoolingOffException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 449;

    public Status449AccountFrozenCoolingOffException(String message) {
        super(ERROR_CODE, message, Status449AccountFrozenCoolingOffException.class.getCanonicalName());
    }
}
