package lithium.service.reward.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status466InvalidRewardStateException extends NotRetryableErrorCodeException {
    public Status466InvalidRewardStateException(String message) {
        super(466, message, Status466InvalidRewardStateException.class.getCanonicalName());
    }
}
