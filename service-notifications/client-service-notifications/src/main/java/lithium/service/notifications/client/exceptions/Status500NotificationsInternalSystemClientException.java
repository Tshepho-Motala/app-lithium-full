package lithium.service.notifications.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status500NotificationsInternalSystemClientException extends NotRetryableErrorCodeException {
    public Status500NotificationsInternalSystemClientException(String message) {
        super(500, message, Status500NotificationsInternalSystemClientException.class.getCanonicalName());
    }
}
