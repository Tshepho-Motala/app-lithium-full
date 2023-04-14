package lithium.service.cashier.mock.inpay.data.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status400InvalidNotificationException extends NotRetryableErrorCodeException {
    public Status400InvalidNotificationException(String message) {
        super(400, Status400InvalidNotificationException.class.getCanonicalName(), message);
    }
}
