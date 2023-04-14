package lithium.service.cashier.mock.smartcash.data.exceptions;

import lithium.service.cashier.processor.smartcash.data.SmartcashConnectionError;

public class SmartcashErrorMessageException extends SmartcashMockException {

    private String error;
    private String errorDescription;

    public SmartcashErrorMessageException(int httpCode, String error, String errorDescription) {
        super(httpCode, errorDescription);
        this.error = error;
        this.errorDescription = errorDescription;
    }

    public SmartcashConnectionError getResponse() {
        return SmartcashConnectionError.
            builder().errorDescription(errorDescription).error(error).build();
    }
}
