package lithium.service.accounting.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status413AccountingCurrencyNotFoundException extends NotRetryableErrorCodeException {
    public static final int CODE = 413;
    public Status413AccountingCurrencyNotFoundException(String currencyString) {
        super(CODE, "Unable to locate currency in accounting: " + currencyString, Status413AccountingCurrencyNotFoundException.class.getCanonicalName());
    }
}
