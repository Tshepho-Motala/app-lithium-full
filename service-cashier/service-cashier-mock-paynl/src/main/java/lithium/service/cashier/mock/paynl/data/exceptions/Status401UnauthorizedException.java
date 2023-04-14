package lithium.service.cashier.mock.paynl.data.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status401UnauthorizedException extends NotRetryableErrorCodeException {
    public Status401UnauthorizedException(String message) {
        super(403, Status401UnauthorizedException.class.getCanonicalName(), message);
    }
}
