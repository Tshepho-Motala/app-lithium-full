package lithium.service.cashier.exceptions;

public class MoreThanOneMethodWithCodeException extends Exception {
    public MoreThanOneMethodWithCodeException() {
        super("More than one method with this code is configured");
    }
}
