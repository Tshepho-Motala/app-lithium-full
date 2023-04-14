package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status400BadRequestException extends NotRetryableErrorCodeException {
    public Status400BadRequestException(String message) {
        super(400, message, Status400BadRequestException.class.getCanonicalName());
    }
}
