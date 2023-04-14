package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status425IllegalUserStateException extends NotRetryableErrorCodeException {
    public Status425IllegalUserStateException(String message) {
        super(454, "Illegal User State, " + message, Status425IllegalUserStateException.class.getCanonicalName());
    }
}
