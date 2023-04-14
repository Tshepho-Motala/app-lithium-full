package lithium.service.document.provider.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412FailToGetJobDetailsException extends NotRetryableErrorCodeException {
    public Status412FailToGetJobDetailsException() {
        super(412, "Fail to get job details", Status412FailToGetJobDetailsException.class.getCanonicalName());
    }
}
