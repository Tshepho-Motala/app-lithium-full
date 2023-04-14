package lithium.service.casino.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status445FailedToToggleExternalBonusRestrictionException extends NotRetryableErrorCodeException {
    public Status445FailedToToggleExternalBonusRestrictionException(String message) {
        super(445, message, Status445FailedToToggleExternalBonusRestrictionException.class.getCanonicalName());
    }
}
