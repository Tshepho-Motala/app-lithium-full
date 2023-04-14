package lithium.service.cdn.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status415InvalidFileUploadException extends NotRetryableErrorCodeException {
    public Status415InvalidFileUploadException(String message) {
        super(415, message, Status415InvalidFileUploadException.class.getCanonicalName());
    }
}