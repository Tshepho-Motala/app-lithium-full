package lithium.service.document.provider.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status414FailFromHelloSodaServiceException extends NotRetryableErrorCodeException {
    public Status414FailFromHelloSodaServiceException(String message) {
        super(414, "Hello soda fail with error: " + message, Status414FailFromHelloSodaServiceException.class.getCanonicalName());
    }
}
