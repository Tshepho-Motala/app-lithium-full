package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status475AnnualLossLimitReachedException extends NotRetryableErrorCodeException {
    public static final int CODE = 475;
    public Status475AnnualLossLimitReachedException(String message) {
        super(CODE, message, Status475AnnualLossLimitReachedException.class.getCanonicalName());
    }
}

