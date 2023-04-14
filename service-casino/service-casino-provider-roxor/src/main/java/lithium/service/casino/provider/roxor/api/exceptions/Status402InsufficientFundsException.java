package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status402InsufficientFundsException extends RoxorErrorCodeException {
    public Status402InsufficientFundsException() {
        super(ErrorCategory.EC_402.category(), ErrorCategory.EC_402.displayMessage(), Status402InsufficientFundsException.class.getCanonicalName());
    }

    public Status402InsufficientFundsException(Object context) {
        super(ErrorCategory.EC_402.category(), ErrorCategory.EC_402.displayMessage(), context,
                Status402InsufficientFundsException.class.getCanonicalName());
    }
}
