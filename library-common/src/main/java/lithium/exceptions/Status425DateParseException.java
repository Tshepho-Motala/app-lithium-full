package lithium.exceptions;

public class Status425DateParseException extends NotRetryableErrorCodeException {
    public Status425DateParseException(String message) {
        super(425, message, Status425DateParseException.class.getCanonicalName());
    }
}
