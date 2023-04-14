package lithium.service.user.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.util.ExceptionMessageUtil;

public class Status500UserInternalSystemClientException extends NotRetryableErrorCodeException {
    public Status500UserInternalSystemClientException(Throwable e) {
        super(500, "User service client exception: " + ExceptionMessageUtil.allMessages(e), e, Status500UserInternalSystemClientException.class.getCanonicalName());
    }

    public Status500UserInternalSystemClientException(String msg) {
        super(500, "User service client exception: " + msg, Status500UserInternalSystemClientException.class.getCanonicalName());
    }
}
