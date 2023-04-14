package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status423OutOfSequenceException extends NotRetryableErrorCodeException {
    public Status423OutOfSequenceException(int expectedSequenceNumber) {
        super(423, "Sequence number out of order for this round. Expected " + expectedSequenceNumber, Status423OutOfSequenceException.class.getCanonicalName());
    }
}
