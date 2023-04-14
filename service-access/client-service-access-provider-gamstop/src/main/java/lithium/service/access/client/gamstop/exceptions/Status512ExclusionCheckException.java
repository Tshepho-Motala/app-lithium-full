package lithium.service.access.client.gamstop.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status512ExclusionCheckException extends NotRetryableErrorCodeException {
    public Status512ExclusionCheckException(String message) {
        super(512, message, Status512ExclusionCheckException.class.getCanonicalName());
    }
}

