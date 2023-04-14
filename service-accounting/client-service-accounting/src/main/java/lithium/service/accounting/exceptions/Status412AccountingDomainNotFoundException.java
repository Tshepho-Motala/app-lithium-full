package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status412AccountingDomainNotFoundException extends NotRetryableErrorCodeException {
    public static final int CODE = 412;
    public Status412AccountingDomainNotFoundException(String domainName) {
        super(CODE, "Unable to locate domain in accounting: " + domainName, Status412AccountingDomainNotFoundException.class.getCanonicalName());
    }
}
