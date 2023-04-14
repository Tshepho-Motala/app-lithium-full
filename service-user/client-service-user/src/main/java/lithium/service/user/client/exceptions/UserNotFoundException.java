package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class UserNotFoundException extends NotRetryableErrorCodeException {

    public UserNotFoundException() {
        super(401, "User not found", UserNotFoundException.class.getCanonicalName());
    }

    public UserNotFoundException(String message) {
        super(401, message, UserNotFoundException.class.getCanonicalName());
    }

    public UserNotFoundException(String message, StackTraceElement[] stackTrace) {
        super(401, message, UserNotFoundException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}