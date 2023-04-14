package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status510AccountingProviderUnavailableException extends NotRetryableErrorCodeException {
    public static final int CODE = 510;
    public Status510AccountingProviderUnavailableException(String message) {
        super(CODE, message, Status510AccountingProviderUnavailableException.class.getCanonicalName());
    }
}
