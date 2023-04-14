package lithium.service.games.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status406DisabledGameException extends NotRetryableErrorCodeException {
    public Status406DisabledGameException(String message) {
        super(406, message, Status406DisabledGameException.class.getCanonicalName());
    }
}