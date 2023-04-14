package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status442LifetimeDepositException extends RoxorErrorCodeException {
    public Status442LifetimeDepositException() {
        super(ErrorCategory.EC_442.category(), ErrorCategory.EC_442.displayMessage(), Status442LifetimeDepositException.class.getCanonicalName());
    }

    public Status442LifetimeDepositException(Object context) {
        super(ErrorCategory.EC_442.category(), ErrorCategory.EC_442.displayMessage(), context,
                Status442LifetimeDepositException.class.getCanonicalName());
    }
}
