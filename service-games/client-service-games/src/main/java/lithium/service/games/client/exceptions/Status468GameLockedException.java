package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status468GameLockedException extends NotRetryableErrorCodeException {

    public Status468GameLockedException(String message) {
        super(468, message, Status468GameLockedException.class.getCanonicalName());
    }

}
