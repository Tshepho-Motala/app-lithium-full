package lithium.service.cashier.processor.paypal.exceptions;

public class PayPalServiceHttpErrorException extends Exception {
    private final static String ERROR_TEXT = "PayPal Service error: ";
    private int httpCode;
    private String body;
    public PayPalServiceHttpErrorException(String message, String body, int httpCode) {
        super(ERROR_TEXT + message);
        this.httpCode = httpCode;
        this.body = body;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }
}
