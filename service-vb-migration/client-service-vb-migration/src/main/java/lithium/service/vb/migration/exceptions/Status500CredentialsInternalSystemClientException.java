package lithium.service.vb.migration.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.util.ExceptionMessageUtil;

public class Status500CredentialsInternalSystemClientException extends NotRetryableErrorCodeException {
    public Status500CredentialsInternalSystemClientException(Throwable e) {
        super(500, "Vb migration service client exception: " + ExceptionMessageUtil.allMessages(e), e, Status500CredentialsInternalSystemClientException.class.getCanonicalName());
    }

    public Status500CredentialsInternalSystemClientException(String msg) {
        super(500, "Vb migration service client exception: " + msg, Status500CredentialsInternalSystemClientException.class.getCanonicalName());
    }
}
