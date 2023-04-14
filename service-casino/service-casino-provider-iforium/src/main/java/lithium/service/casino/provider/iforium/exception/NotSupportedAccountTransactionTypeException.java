package lithium.service.casino.provider.iforium.exception;

import static java.lang.String.format;

public class NotSupportedAccountTransactionTypeException extends RuntimeException {

    public NotSupportedAccountTransactionTypeException(String accountTransactionTypeId) {
        super(format("accountTransactionTypeId=%s is not supported", accountTransactionTypeId));
    }
}
