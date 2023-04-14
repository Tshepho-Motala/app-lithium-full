package lithium.service.cashier.processor.trustly.api.exceptions;

public class TrustlyAPIException extends RuntimeException {

    public TrustlyAPIException() {

    }

    public TrustlyAPIException(final Throwable t) {
        super(t);
    }

    public TrustlyAPIException(final String message) {
        super(message);
    }

    public TrustlyAPIException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
