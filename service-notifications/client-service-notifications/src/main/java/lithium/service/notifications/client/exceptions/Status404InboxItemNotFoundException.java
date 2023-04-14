package lithium.service.notifications.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404InboxItemNotFoundException extends NotRetryableErrorCodeException {
    public Status404InboxItemNotFoundException(String message) {
        super(404, message, Status404InboxItemNotFoundException.class.getCanonicalName());
    }
}
