package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;

public class Status400BadRequestException extends RoxorErrorCodeException {
    public Status400BadRequestException() {
        super(ErrorCategory.EC_400.category(), ErrorCategory.EC_400.displayMessage(), Status400BadRequestException.class.getCanonicalName());
    }

    public Status400BadRequestException(Object context) {
        super(ErrorCategory.EC_400.category(), ErrorCategory.EC_400.displayMessage(), context,
                Status400BadRequestException.class.getCanonicalName());
    }
}
