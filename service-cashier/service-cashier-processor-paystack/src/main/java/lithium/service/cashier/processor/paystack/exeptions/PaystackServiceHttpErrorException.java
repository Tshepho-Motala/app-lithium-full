package lithium.service.cashier.processor.paystack.exeptions;

public class PaystackServiceHttpErrorException extends Exception {
    private int httpCode;
    public PaystackServiceHttpErrorException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
