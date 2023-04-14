package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status490SoftSelfExclusionException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 490;
    public Status490SoftSelfExclusionException(String message, Object context) {
        super(ERROR_CODE, message, context, Status490SoftSelfExclusionException.class.getCanonicalName());
    }

    public Status490SoftSelfExclusionException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status490SoftSelfExclusionException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }

    public Status490SoftSelfExclusionException(String message) {
        super(ERROR_CODE, message, Status490SoftSelfExclusionException.class.getCanonicalName());
    }
}
