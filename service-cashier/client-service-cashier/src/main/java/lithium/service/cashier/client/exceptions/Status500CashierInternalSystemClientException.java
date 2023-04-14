package lithium.service.cashier.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.util.ExceptionMessageUtil;

public class Status500CashierInternalSystemClientException extends NotRetryableErrorCodeException {
    public static final int ERROR_CODE = 500;
    public Status500CashierInternalSystemClientException(Throwable e) {
        super(ERROR_CODE, "Cashier service client exception: " + ExceptionMessageUtil.allMessages(e), e, Status500CashierInternalSystemClientException.class.getCanonicalName());
    }

    public Status500CashierInternalSystemClientException(String message) {
        super(ERROR_CODE, message, Status500CashierInternalSystemClientException.class.getCanonicalName());
    }

    public Status500CashierInternalSystemClientException(String message, StackTraceElement[] stackTrace) {
        super(ERROR_CODE, message, Status500CashierInternalSystemClientException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
