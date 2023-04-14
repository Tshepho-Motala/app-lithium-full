package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status444DepositLimitException extends RoxorErrorCodeException {
    public Status444DepositLimitException() {
        super(ErrorCategory.EC_444.category(), ErrorCategory.EC_444.displayMessage(), Status444DepositLimitException.class.getCanonicalName());
    }

    public Status444DepositLimitException(Object context) {
        super(ErrorCategory.EC_444.category(), ErrorCategory.EC_444.displayMessage(), context,
                Status444DepositLimitException.class.getCanonicalName());
    }
}
