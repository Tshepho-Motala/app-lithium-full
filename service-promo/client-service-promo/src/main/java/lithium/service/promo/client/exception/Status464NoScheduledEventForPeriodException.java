package lithium.service.promo.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status464NoScheduledEventForPeriodException extends NotRetryableErrorCodeException {
    public static final int CODE = 464;
    public Status464NoScheduledEventForPeriodException() {
        super(CODE, "", Status464NoScheduledEventForPeriodException.class.getCanonicalName());
    }
}
