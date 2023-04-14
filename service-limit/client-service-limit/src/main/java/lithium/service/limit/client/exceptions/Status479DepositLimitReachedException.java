package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.util.ExceptionMessageUtil;

public class Status479DepositLimitReachedException  extends NotRetryableErrorCodeException {
    public static final int CODE = 479;
    public Status479DepositLimitReachedException(Throwable e) {
        super(CODE, "Limit service client exception: " + ExceptionMessageUtil.allMessages(e), e, Status479DepositLimitReachedException.class.getCanonicalName());
    }

    public Status479DepositLimitReachedException(String message) {
        super(CODE, message, Status479DepositLimitReachedException.class.getCanonicalName());
    }
}
