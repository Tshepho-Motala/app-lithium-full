package lithium.exceptions;

public class Status462WithdrawRestrictedException extends NotRetryableErrorCodeException {
	public Status462WithdrawRestrictedException() {
		super(462, "Withdraw restricted", Status462WithdrawRestrictedException.class.getCanonicalName());
	}

	public Status462WithdrawRestrictedException(String message) {
		super(462, message, Status462WithdrawRestrictedException.class.getCanonicalName());
	}
}
