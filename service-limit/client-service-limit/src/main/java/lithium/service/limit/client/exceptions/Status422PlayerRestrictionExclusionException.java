package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status422PlayerRestrictionExclusionException extends NotRetryableErrorCodeException {
    public Status422PlayerRestrictionExclusionException(String message) {
        super(422, message, Status422PlayerRestrictionExclusionException.class.getCanonicalName());
    }
}
