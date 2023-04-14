package lithium.service.cashier.exceptions;

public class TrasactionInFinalStateException extends Exception {
    public TrasactionInFinalStateException() {
        super("Transaction is in final state.");
    }
}
