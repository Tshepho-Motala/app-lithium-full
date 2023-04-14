package lithium.service.cashier.processor.bluem.exceptions;

public class BluemConnectionException extends Exception {

    private int httpErrorCode;

    public BluemConnectionException(int httpErrorCode, String message) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }

    public int getHttpErrorCode() { return httpErrorCode; }
}
