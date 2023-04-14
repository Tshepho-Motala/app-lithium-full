package lithium.exceptions;

public class Status413EcosystemNotFoundException extends NotRetryableErrorCodeException{
    public static final int CODE = 413;
    public Status413EcosystemNotFoundException(String message) {
        super(CODE, message, Status413EcosystemNotFoundException.class.getCanonicalName());
    }
}
