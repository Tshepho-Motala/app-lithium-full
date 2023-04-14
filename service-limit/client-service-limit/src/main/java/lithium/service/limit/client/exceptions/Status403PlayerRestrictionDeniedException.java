package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status403PlayerRestrictionDeniedException extends NotRetryableErrorCodeException {
    public static final int CODE = 403;
    public Status403PlayerRestrictionDeniedException(String message) {
        super(CODE, message,null, Status403PlayerRestrictionDeniedException.class.getCanonicalName());
    }

    public Status403PlayerRestrictionDeniedException(String message, String globallyUniqueErrorIdentifier) {
        super(CODE, message,null, globallyUniqueErrorIdentifier);
    }
}
