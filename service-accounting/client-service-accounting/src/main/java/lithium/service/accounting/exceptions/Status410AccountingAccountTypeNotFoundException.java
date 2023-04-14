package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status410AccountingAccountTypeNotFoundException extends NotRetryableErrorCodeException {
    public static final int CODE = 410;
    public Status410AccountingAccountTypeNotFoundException(String accountTypeString) {
        super(CODE, "Unable to find account type: " + accountTypeString, Status410AccountingAccountTypeNotFoundException.class.getCanonicalName());
    }
}
