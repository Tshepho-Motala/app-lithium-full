package lithium.service.cashier.processor.trustly.api.exceptions;

public class TrustlySignatureException extends TrustlyAPIException {

    public TrustlySignatureException() {
    }

    public TrustlySignatureException(final String message) {
        super(message);
    }

    public TrustlySignatureException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TrustlySignatureException(final Throwable e) {
        super(e);
    }
}
