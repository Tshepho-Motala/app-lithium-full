package lithium.service.cashier.mock.smartcash.data.exceptions;

public abstract class SmartcashMockException extends RuntimeException {

    protected int httpCode;

    public SmartcashMockException(int code, String message) {
        super(message);
        httpCode = code;
    }

    public int getHttpCode() {
        return httpCode;
    }

}
