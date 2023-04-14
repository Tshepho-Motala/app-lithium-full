package lithium.service.casino.provider.roxor.api.exceptions;

import lithium.exceptions.ErrorCodeException;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorCategory;
import lithium.util.ExceptionMessageUtil;

public class Status500RuntimeException extends ErrorCodeException {
    public Status500RuntimeException() {
        super(ErrorCategory.EC_500.category(), ErrorCategory.EC_500.displayMessage(), Status500RuntimeException.class.getCanonicalName());
    }

    public Status500RuntimeException(Object context) {
        super(ErrorCategory.EC_500.category(), ErrorCategory.EC_500.displayMessage(), context,
                Status500RuntimeException.class.getCanonicalName());
    }

    public Status500RuntimeException(String message, Throwable e) {
        super(500, message + " : " + ExceptionMessageUtil.allMessages(e), e, Status500RuntimeException.class.getCanonicalName());
    }
}
