package lithium.service.cashier.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status439BankAccountLookupClientException extends NotRetryableErrorCodeException {

    public static final int CODE = 439;

    public Status439BankAccountLookupClientException(String message) {
        super(CODE, message, Status439BankAccountLookupClientException.class.getCanonicalName());
    }
}
