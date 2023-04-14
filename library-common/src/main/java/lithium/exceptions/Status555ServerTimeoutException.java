package lithium.exceptions;

public class Status555ServerTimeoutException extends NotRetryableErrorCodeException {

    public Status555ServerTimeoutException(String message) {
        super(555, message, Status555ServerTimeoutException.class.getCanonicalName());
    }
}
