package lithium.service.document.provider.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status413FailToGetSessionTokenFromHelloSodaServiceException extends NotRetryableErrorCodeException {
    public Status413FailToGetSessionTokenFromHelloSodaServiceException() {
        super(413, "Can't create hello soda session token", Status413FailToGetSessionTokenFromHelloSodaServiceException.class.getCanonicalName());
    }
}
