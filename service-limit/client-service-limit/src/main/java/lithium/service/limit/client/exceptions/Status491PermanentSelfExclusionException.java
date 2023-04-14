package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status491PermanentSelfExclusionException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 491;
    public Status491PermanentSelfExclusionException(String message, Object context) {
        super(ERROR_CODE, message, context, Status491PermanentSelfExclusionException.class.getCanonicalName());
    }

    public Status491PermanentSelfExclusionException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status491PermanentSelfExclusionException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }

    public Status491PermanentSelfExclusionException(String message) {
        super(ERROR_CODE, message, Status491PermanentSelfExclusionException.class.getCanonicalName());
    }
}
