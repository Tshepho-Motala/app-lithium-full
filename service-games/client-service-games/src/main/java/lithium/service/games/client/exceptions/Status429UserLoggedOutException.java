package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status429UserLoggedOutException extends NotRetryableErrorCodeException {
    public Status429UserLoggedOutException(String message) {
        super(429, message, Status429UserLoggedOutException.class.getCanonicalName());
    }

    public Status429UserLoggedOutException() {
        super(429, "User session no longer valid", Status429UserLoggedOutException.class.getCanonicalName());
    }
}
