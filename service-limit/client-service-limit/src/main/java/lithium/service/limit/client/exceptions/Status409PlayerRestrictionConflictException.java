package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status409PlayerRestrictionConflictException extends NotRetryableErrorCodeException {
    public static final int CODE = 409;
    public Status409PlayerRestrictionConflictException(String message) {
        super(CODE, message,null, Status409PlayerRestrictionConflictException.class.getCanonicalName());
    }

    public Status409PlayerRestrictionConflictException(String message, String globallyUniqueErrorIdentifier) {
        super(CODE, message,null, globallyUniqueErrorIdentifier);
    }
}
