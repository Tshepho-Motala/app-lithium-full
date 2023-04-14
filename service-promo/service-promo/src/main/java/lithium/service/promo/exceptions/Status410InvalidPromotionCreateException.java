package lithium.service.promo.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status410InvalidPromotionCreateException extends NotRetryableErrorCodeException {
    public static final int CODE = 410;
    public Status410InvalidPromotionCreateException() {
        super(CODE, "", Status410InvalidPromotionCreateException.class.getCanonicalName());
    }
}
