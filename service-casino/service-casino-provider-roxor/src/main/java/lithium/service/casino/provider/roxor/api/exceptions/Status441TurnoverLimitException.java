package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status441TurnoverLimitException extends RoxorErrorCodeException {
    public Status441TurnoverLimitException() {
        super(ErrorCategory.EC_441.category(), ErrorCategory.EC_441.displayMessage(), Status441TurnoverLimitException.class.getCanonicalName());
    }

    public Status441TurnoverLimitException(Object context) {
        super(ErrorCategory.EC_441.category(), ErrorCategory.EC_441.displayMessage(), context,
                Status441TurnoverLimitException.class.getCanonicalName());
    }
}
