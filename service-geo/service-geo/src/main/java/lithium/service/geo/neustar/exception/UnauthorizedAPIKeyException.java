package lithium.service.geo.neustar.exception;

public class UnauthorizedAPIKeyException extends RuntimeException {
    public UnauthorizedAPIKeyException() {
        super("Unauthorised");
    }
}
