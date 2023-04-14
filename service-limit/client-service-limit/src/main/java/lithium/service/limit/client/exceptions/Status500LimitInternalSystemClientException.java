package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.util.ExceptionMessageUtil;

public class Status500LimitInternalSystemClientException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 500;
    public Status500LimitInternalSystemClientException(Throwable e) {
        super(ERROR_CODE, "Limit service client exception: " + ExceptionMessageUtil.allMessages(e), e, Status500LimitInternalSystemClientException.class.getCanonicalName());
    }

    public Status500LimitInternalSystemClientException(String message) {
        super(ERROR_CODE, message, Status500LimitInternalSystemClientException.class.getCanonicalName());
    }

    public Status500LimitInternalSystemClientException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status500LimitInternalSystemClientException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
