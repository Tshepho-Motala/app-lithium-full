package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status414AccountingTransactionDataValidationException extends NotRetryableErrorCodeException {
    public static final int CODE = 414;
    public Status414AccountingTransactionDataValidationException(String message) {
        super(CODE, message, Status414AccountingTransactionDataValidationException.class.getCanonicalName());
    }
}
