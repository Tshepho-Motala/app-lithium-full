package lithium.service.promo.client.exception;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411InvalidPromotionException extends NotRetryableErrorCodeException {
    public static final int CODE = 411;
    public Status411InvalidPromotionException() {
        super(CODE, "Invalid promotion provided", Status411InvalidPromotionException.class.getCanonicalName());
    }
}
