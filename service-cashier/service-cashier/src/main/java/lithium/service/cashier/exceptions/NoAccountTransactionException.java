package lithium.service.cashier.exceptions;

public class NoAccountTransactionException extends Exception {
    public NoAccountTransactionException() {
        super("No account transaction exception");
    }
}
