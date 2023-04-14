package lithium.service.domain.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.util.ExceptionMessageUtil;

public class Status550ServiceDomainClientException extends NotRetryableErrorCodeException {
    public Status550ServiceDomainClientException(String message) {
        super(550, message, Status550ServiceDomainClientException.class.getCanonicalName());
    }

    public Status550ServiceDomainClientException(String message, StackTraceElement[] stackTrace) {
        super(550, message, Status550ServiceDomainClientException.class.getCanonicalName());
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }

    public Status550ServiceDomainClientException(LithiumServiceClientFactoryException e) {
        super(550, ExceptionMessageUtil.allMessages(e), e, Status550ServiceDomainClientException.class.getCanonicalName());
    }
}
