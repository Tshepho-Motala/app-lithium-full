package lithium.service.cashier.mock.hexopay.data.exceptions;

public abstract class HexopayMockException extends RuntimeException {

    private int httpCode;

    public HexopayMockException(int code, String message) {
        super(message);
        httpCode = code;
    }

    public int getHttpCode() {
        return httpCode;
    }

}
