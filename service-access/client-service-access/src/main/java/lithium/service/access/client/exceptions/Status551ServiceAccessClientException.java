package lithium.service.access.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.util.ExceptionMessageUtil;

public class Status551ServiceAccessClientException extends NotRetryableErrorCodeException {
    public Status551ServiceAccessClientException(String message) {
        super(551, message, Status551ServiceAccessClientException.class.getCanonicalName());
    }

    public Status551ServiceAccessClientException(LithiumServiceClientFactoryException e) {
        super(551, ExceptionMessageUtil.allMessages(e), e, Status551ServiceAccessClientException.class.getCanonicalName());
    }
}
