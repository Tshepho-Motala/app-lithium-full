package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status476DomainBalanceLimitDisabledException extends NotRetryableErrorCodeException {
    public static final int CODE = 478;
    public Status476DomainBalanceLimitDisabledException(String message) {
        super(CODE, message, null, Status476DomainBalanceLimitDisabledException.class.getCanonicalName());
    }
}
