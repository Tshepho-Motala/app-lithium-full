package lithium.exceptions;

public class Status426InvalidParameterProvidedException extends NotRetryableErrorCodeException {
    public Status426InvalidParameterProvidedException(String message) {
        super(426, message, Status426InvalidParameterProvidedException.class.getCanonicalName());
    }
}
