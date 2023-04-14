package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status413RetrieveDocumentException extends NotRetryableErrorCodeException {
    public Status413RetrieveDocumentException(String message) {
        super(413, message, Status413RetrieveDocumentException.class.getCanonicalName());
    }
}
