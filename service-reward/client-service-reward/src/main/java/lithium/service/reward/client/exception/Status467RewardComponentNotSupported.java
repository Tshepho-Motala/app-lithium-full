package lithium.service.reward.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status467RewardComponentNotSupported extends NotRetryableErrorCodeException {
    public Status467RewardComponentNotSupported(String message) {
        super(467, message, Status467RewardComponentNotSupported.class.getCanonicalName());
    }
}
