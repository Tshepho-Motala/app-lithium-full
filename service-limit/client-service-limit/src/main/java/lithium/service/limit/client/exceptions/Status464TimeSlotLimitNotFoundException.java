package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status464TimeSlotLimitNotFoundException extends NotRetryableErrorCodeException {
    public Status464TimeSlotLimitNotFoundException(String message) {
        super(464, message, Status464TimeSlotLimitNotFoundException.class.getCanonicalName());
    }
}
