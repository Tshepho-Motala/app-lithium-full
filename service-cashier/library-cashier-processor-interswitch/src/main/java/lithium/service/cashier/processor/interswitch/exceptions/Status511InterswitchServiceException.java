package lithium.service.cashier.processor.interswitch.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status511InterswitchServiceException extends NotRetryableErrorCodeException {

    public Status511InterswitchServiceException(String message) {
        super(511, message, Status511InterswitchServiceException.class.getCanonicalName());
    }

    public Status511InterswitchServiceException(String message, Throwable cause) {
        super(511, message, cause, Status511InterswitchServiceException.class.getCanonicalName());
    }
}
