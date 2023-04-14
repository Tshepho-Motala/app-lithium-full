package lithium.service.cashier.exceptions;

public class NoMethodWithCodeException extends Exception {
    public NoMethodWithCodeException() {
        super("No method with this code is configured");
    }

    public NoMethodWithCodeException(String error) {
        super(error);
    }
}
