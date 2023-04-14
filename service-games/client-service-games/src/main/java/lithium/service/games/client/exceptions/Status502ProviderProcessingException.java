package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status502ProviderProcessingException extends NotRetryableErrorCodeException {
    public Status502ProviderProcessingException(String message) {
        super(502, message, Status502ProviderProcessingException.class.getCanonicalName());
    }

    public Status502ProviderProcessingException() {
        super(502, "Internal Processing Exception by Provider", Status502ProviderProcessingException.class.getCanonicalName());
    }
}
