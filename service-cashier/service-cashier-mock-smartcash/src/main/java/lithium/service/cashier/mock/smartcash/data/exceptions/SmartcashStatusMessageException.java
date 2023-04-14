package lithium.service.cashier.mock.smartcash.data.exceptions;

import lithium.service.cashier.processor.smartcash.data.SmartcashConnectionError;

public class SmartcashStatusMessageException extends SmartcashMockException {

    private String statusCode = "ROUTER003";
    public SmartcashStatusMessageException(int httpCode, String message) {
        super(httpCode, message);
    }
    public SmartcashStatusMessageException(int httpCode, String message, String statusCode) {
        super(httpCode, message);
        this.statusCode = statusCode;
    }

    public SmartcashConnectionError getResponse() {
        return SmartcashConnectionError.
            builder().statusMessage(getMessage()).statusCode("ROUTER003").build();
    }
}
