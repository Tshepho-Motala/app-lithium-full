package lithium.service.cashier.mock.inpay.data.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status401UnauthorizedException extends NotRetryableErrorCodeException {
    public Status401UnauthorizedException(String message) {
        super(401, message, Status401UnauthorizedException.class.getCanonicalName());
    }
}
