package lithium.service.casino.provider.incentive.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404BetTransactionNotFoundException extends NotRetryableErrorCodeException {
    public Status404BetTransactionNotFoundException() {
        super(404, "Bet transaction referenced could not be found", Status404BetTransactionNotFoundException.class.getCanonicalName());
    }
}
