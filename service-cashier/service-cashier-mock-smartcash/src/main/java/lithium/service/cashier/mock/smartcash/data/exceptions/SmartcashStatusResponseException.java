package lithium.service.cashier.mock.smartcash.data.exceptions;

import lithium.service.cashier.processor.smartcash.data.SmartcashResponseStatus;

public class SmartcashStatusResponseException extends SmartcashMockException {
    private String responseCode;

    public SmartcashStatusResponseException(int httpCode, String message, String responseCode) {
        super(httpCode, message);
        this.responseCode = responseCode;
    }

    public SmartcashResponseStatus getResponseStatus() {
        return SmartcashResponseStatus.builder()
            .code(Integer.toString(getHttpCode()))
            .responseCode(responseCode)
            .success(false)
            .message(getMessage())
            .build();
    }
}
