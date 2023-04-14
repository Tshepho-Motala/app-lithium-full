package lithium.service.access.provider.transunion.exeptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.util.ExceptionMessageUtil;

public class UserIndividualsNotSetupException extends NotRetryableErrorCodeException {
    public UserIndividualsNotSetupException(int code, Throwable e) {
        super(code, "An error occurred during an attempt to verify the user using the Transunion KYC service: " + ExceptionMessageUtil.allMessages(e), e, UserIndividualsNotSetupException.class.getCanonicalName());
    }

    public UserIndividualsNotSetupException(int code, String msg) {
        super(code, "An error occurred during an attempt to verify the user using the Transunion KYC service: " + msg, UserIndividualsNotSetupException.class.getCanonicalName());
    }
}
