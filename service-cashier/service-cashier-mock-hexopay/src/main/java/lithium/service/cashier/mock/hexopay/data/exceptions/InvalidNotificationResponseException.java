package lithium.service.cashier.mock.hexopay.data.exceptions;

public class InvalidNotificationResponseException extends HexopayMockException {
    public InvalidNotificationResponseException() {
        super(400, "Invalid notification response");
    }
    public InvalidNotificationResponseException(String message) {
        super(400, message);
    }
}
