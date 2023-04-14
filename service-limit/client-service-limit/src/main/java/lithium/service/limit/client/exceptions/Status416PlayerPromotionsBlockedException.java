package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status416PlayerPromotionsBlockedException extends NotRetryableErrorCodeException {
    public static final int CODE = 416;
    public Status416PlayerPromotionsBlockedException(String message) {
        super(CODE, message,null, Status416PlayerPromotionsBlockedException.class.getCanonicalName());
    }
}
