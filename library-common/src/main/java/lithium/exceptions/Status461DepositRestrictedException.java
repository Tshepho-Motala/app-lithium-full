package lithium.exceptions;

public class Status461DepositRestrictedException extends NotRetryableErrorCodeException {
	public Status461DepositRestrictedException() {
		super(461, "Deposit restricted", Status461DepositRestrictedException.class.getCanonicalName());
	}

	public Status461DepositRestrictedException(String message) {
		super(461, message, Status461DepositRestrictedException.class.getCanonicalName());
	}
}
