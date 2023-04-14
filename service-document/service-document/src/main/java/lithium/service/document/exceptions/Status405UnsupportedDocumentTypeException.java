package lithium.service.document.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status405UnsupportedDocumentTypeException extends NotRetryableErrorCodeException {
    public Status405UnsupportedDocumentTypeException(String message) {
        super(405, message, Status405UnsupportedDocumentTypeException.class.getCanonicalName());
    }
}
