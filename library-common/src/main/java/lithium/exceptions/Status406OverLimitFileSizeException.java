package lithium.exceptions;

public class Status406OverLimitFileSizeException extends NotRetryableErrorCodeException {
    public Status406OverLimitFileSizeException(String message) {
        super(406, message, Status406OverLimitFileSizeException.class.getCanonicalName());
    }
}