package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status477DomainTimeSlotLimitDisabledException extends NotRetryableErrorCodeException {
    public Status477DomainTimeSlotLimitDisabledException(String message) {
        super(477, message, Status477DomainTimeSlotLimitDisabledException.class.getCanonicalName());
    }
}
