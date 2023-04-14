package lithium.service.casino.provider.iforium.exception;

public class SessionKeyExpiredException extends RuntimeException {

    public SessionKeyExpiredException(String message) {
        super(message);
    }
}
