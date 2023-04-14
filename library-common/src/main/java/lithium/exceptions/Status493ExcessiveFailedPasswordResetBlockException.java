package lithium.exceptions;

public class Status493ExcessiveFailedPasswordResetBlockException extends NotRetryableErrorCodeException {

    public Status493ExcessiveFailedPasswordResetBlockException(String message) {
        super(493, message, Status493ExcessiveFailedPasswordResetBlockException.class.getCanonicalName());
    }
}
