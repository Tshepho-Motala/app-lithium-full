package lithium.service.cashier.client.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status515CallbackGetTransactionClientException extends ErrorCodeException {

    public static final int CODE = 515;
    public Status515CallbackGetTransactionClientException(String message) {
        super(CODE, message, Status515CallbackGetTransactionClientException.class.getCanonicalName());
    }
}
