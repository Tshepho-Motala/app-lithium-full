package lithium.service.cashier.processor.trustly.api.exceptions;

public class TrustlyConnectionException extends TrustlyAPIException {

    public TrustlyConnectionException() {
    }

    public TrustlyConnectionException(final String message) {
        super(message);
    }

    public TrustlyConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
