package lithium.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status432UserExistsInAnotherExclusiveDomainException extends NotRetryableErrorCodeException {
    public Status432UserExistsInAnotherExclusiveDomainException(String message) {
        super(432, message, Status432UserExistsInAnotherExclusiveDomainException.class.getCanonicalName());
    }
}
