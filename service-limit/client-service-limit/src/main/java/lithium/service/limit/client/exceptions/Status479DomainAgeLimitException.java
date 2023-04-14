package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status479DomainAgeLimitException extends NotRetryableErrorCodeException {
    public Status479DomainAgeLimitException() { super(479, "Error on domain age limit", Status479DomainAgeLimitException.class.getCanonicalName()); }
    public Status479DomainAgeLimitException(String message) { super(479, message, Status479DomainAgeLimitException.class.getCanonicalName()); }
}
