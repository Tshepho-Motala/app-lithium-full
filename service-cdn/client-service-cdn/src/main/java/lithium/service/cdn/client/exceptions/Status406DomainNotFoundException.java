package lithium.service.cdn.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status406DomainNotFoundException extends NotRetryableErrorCodeException {
    public Status406DomainNotFoundException(String message) {
        super(406, message, Status406DomainNotFoundException.class.getCanonicalName());
    }
}
