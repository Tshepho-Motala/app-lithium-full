package lithium.service.kyc.provider.onfido.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status415NoDocumentToCheckException extends NotRetryableErrorCodeException {
    public Status415NoDocumentToCheckException(String message) {
        super(415, message, Status415NoDocumentToCheckException.class.getCanonicalName());
    }
}
