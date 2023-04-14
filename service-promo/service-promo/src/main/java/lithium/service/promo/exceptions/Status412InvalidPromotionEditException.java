package lithium.service.promo.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412InvalidPromotionEditException extends NotRetryableErrorCodeException {
    public static final int CODE = 412;
    public Status412InvalidPromotionEditException() {
        super(CODE, "", Status412InvalidPromotionEditException.class.getCanonicalName());
    }
}
