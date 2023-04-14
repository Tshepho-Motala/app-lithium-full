package lithium.service.cashier.mock.smartcash.data.exceptions;

public class InvalidNotificationResponseException extends SmartcashMockException {
    public InvalidNotificationResponseException() {
        super(400, "Invalid notification response");
    }
    public InvalidNotificationResponseException(String message) {
        super(400, message);
    }
}
