package lithium.service.casino.provider.iforium.exception;

public class SessionTokenExpiredException extends RuntimeException {

    public SessionTokenExpiredException(String message) {
        super(message);
    }
}
