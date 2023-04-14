package lithium.service.games.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status469DepositRequiredException extends NotRetryableErrorCodeException {

    public Status469DepositRequiredException(String message) {
        super(469, message, Status469DepositRequiredException.class.getCanonicalName());
    }

}
