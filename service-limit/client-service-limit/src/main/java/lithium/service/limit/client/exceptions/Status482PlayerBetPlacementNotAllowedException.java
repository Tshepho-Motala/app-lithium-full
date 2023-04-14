package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status482PlayerBetPlacementNotAllowedException extends NotRetryableErrorCodeException {
    public static final int CODE = 482;
    public Status482PlayerBetPlacementNotAllowedException(String message) {
        super(CODE, message, Status482PlayerBetPlacementNotAllowedException.class.getCanonicalName());
    }
}
