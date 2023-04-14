package lithium.service.cashier.exceptions;

public class AccountTransactionLableAlredyExistsException extends Exception {
    public AccountTransactionLableAlredyExistsException() {
        super("Account transaction label already exists.");
    }
}
