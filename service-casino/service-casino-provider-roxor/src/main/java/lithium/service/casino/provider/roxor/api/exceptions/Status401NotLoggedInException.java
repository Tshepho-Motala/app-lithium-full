package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status401NotLoggedInException extends RoxorErrorCodeException {
    public Status401NotLoggedInException() {
        super(ErrorCategory.EC_401.category(), ErrorCategory.EC_401.displayMessage(), Status401NotLoggedInException.class.getCanonicalName());
    }

    public Status401NotLoggedInException(Object context) {
        super(ErrorCategory.EC_401.category(), ErrorCategory.EC_401.displayMessage(), context,
                Status401NotLoggedInException.class.getCanonicalName());
    }
}
