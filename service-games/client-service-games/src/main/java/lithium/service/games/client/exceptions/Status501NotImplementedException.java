package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status501NotImplementedException extends NotRetryableErrorCodeException {
    public Status501NotImplementedException(String message) {
        super(501, message, Status501NotImplementedException.class.getCanonicalName());
    }

    public Status501NotImplementedException() {
        super(501, "Not yet implemented", Status501NotImplementedException.class.getCanonicalName());
    }
}
