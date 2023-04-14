package lithium.service.access.client.gamstop.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status424InvalidRequestException extends NotRetryableErrorCodeException {
    public Status424InvalidRequestException(String message) {
        super(424, message, Status424InvalidRequestException.class.getCanonicalName());
    }
}
