package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status404NotFoundException extends RoxorErrorCodeException {
    public Status404NotFoundException() {
        super(ErrorCategory.EC_404.category(), ErrorCategory.EC_404.displayMessage(), Status404NotFoundException.class.getCanonicalName());
    }

    public Status404NotFoundException(Object context) {
        super(ErrorCategory.EC_404.category(), ErrorCategory.EC_404.displayMessage(), context,
                Status404NotFoundException.class.getCanonicalName());
    }
}
