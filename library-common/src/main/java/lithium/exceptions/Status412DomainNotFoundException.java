package lithium.exceptions;

public class Status412DomainNotFoundException extends NotRetryableErrorCodeException{
    public static final int CODE = 412;
    public Status412DomainNotFoundException(String message) {
        super(CODE, message, Status412DomainNotFoundException.class.getCanonicalName());
    }
}

