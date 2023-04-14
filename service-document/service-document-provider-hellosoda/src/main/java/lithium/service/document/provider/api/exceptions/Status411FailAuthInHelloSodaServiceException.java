package lithium.service.document.provider.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411FailAuthInHelloSodaServiceException extends NotRetryableErrorCodeException {
    public Status411FailAuthInHelloSodaServiceException() {
        super(411, "Failed to authenticate and get api token from Hello soda service", Status411FailAuthInHelloSodaServiceException.class.getCanonicalName());
    }
}
