package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status478TimeSlotLimitException extends NotRetryableErrorCodeException {
    public Status478TimeSlotLimitException(String message) {
        super(478, message, Status478TimeSlotLimitException.class.getCanonicalName());
    }
}
