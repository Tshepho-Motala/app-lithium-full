package lithium.exceptions;

public class Status470HashInvalidException extends NotRetryableErrorCodeException {
    public static final int CODE = 470;
    public Status470HashInvalidException(String message) {
        super(CODE, message, Status470HashInvalidException.class.getCanonicalName());
    }

    public Status470HashInvalidException() {
        super(CODE, "Invalid hash", Status470HashInvalidException.class.getCanonicalName());
    }
}
