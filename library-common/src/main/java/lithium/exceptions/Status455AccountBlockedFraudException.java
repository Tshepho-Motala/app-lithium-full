package lithium.exceptions;

public class Status455AccountBlockedFraudException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 455;

    public Status455AccountBlockedFraudException(String message) {
        super(ERROR_CODE, message, Status455AccountBlockedFraudException.class.getCanonicalName());
    }
}
