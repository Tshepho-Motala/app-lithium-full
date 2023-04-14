package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status411AccountingUserNotFoundException extends NotRetryableErrorCodeException {
    public static final int CODE = 411;
    public Status411AccountingUserNotFoundException(String userGuid) {
        super(CODE, "Unable to locate user in accounting: " + userGuid, Status411AccountingUserNotFoundException.class.getCanonicalName());
    }
}
