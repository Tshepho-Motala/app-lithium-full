package lithium.exceptions;

public class Status463IncompleteUserRegistrationException extends NotRetryableErrorCodeException {
    public Status463IncompleteUserRegistrationException(String message) {
        super(463, message, Status463IncompleteUserRegistrationException.class.getCanonicalName());
    }
}
