package lithium.client.changelog.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.util.ExceptionMessageUtil;

public class Status551ServiceChangeLogClientException extends NotRetryableErrorCodeException {
    public Status551ServiceChangeLogClientException(String message) {
        super(551, message, Status551ServiceChangeLogClientException.class.getCanonicalName());
    }

    public Status551ServiceChangeLogClientException(LithiumServiceClientFactoryException e) {
        super(551, ExceptionMessageUtil.allMessages(e), e, Status551ServiceChangeLogClientException.class.getCanonicalName());
    }
}
