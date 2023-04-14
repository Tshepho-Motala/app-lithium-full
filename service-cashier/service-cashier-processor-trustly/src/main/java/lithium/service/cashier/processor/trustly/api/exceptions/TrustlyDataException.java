package lithium.service.cashier.processor.trustly.api.exceptions;

public class TrustlyDataException extends TrustlyAPIException {

    public TrustlyDataException() {
    }

    public TrustlyDataException(final String message) {
        super(message);
    }

    public TrustlyDataException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TrustlyDataException(final Throwable e) {
        super(e);
    }
}
