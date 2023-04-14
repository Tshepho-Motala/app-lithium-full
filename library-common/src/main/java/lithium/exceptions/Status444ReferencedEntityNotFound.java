package lithium.exceptions;

public class Status444ReferencedEntityNotFound extends NotRetryableErrorCodeException {
    public static final int CODE = 444;
    public Status444ReferencedEntityNotFound(String message) {
        super(CODE, message, Status444ReferencedEntityNotFound.class.getCanonicalName());
    }
}
