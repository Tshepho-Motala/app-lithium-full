package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411GameNotFoundException extends NotRetryableErrorCodeException {
    public Status411GameNotFoundException(String message) {
        super(411, message, Status411GameNotFoundException.class.getCanonicalName());
    }
}
