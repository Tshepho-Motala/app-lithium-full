package lithium.service.cashier.exceptions;

public class BalanceGetFailedException extends RuntimeException {
	public BalanceGetFailedException(String cause) {
		super("Balance get failed. " + cause);
	}
}
