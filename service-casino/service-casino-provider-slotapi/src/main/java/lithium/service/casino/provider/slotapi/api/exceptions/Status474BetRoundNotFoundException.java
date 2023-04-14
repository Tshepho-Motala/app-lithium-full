package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status474BetRoundNotFoundException extends NotRetryableErrorCodeException {
    public Status474BetRoundNotFoundException() {
        super(474, "Bet round referenced could not be found", Status474BetRoundNotFoundException.class.getCanonicalName());
    }
}
