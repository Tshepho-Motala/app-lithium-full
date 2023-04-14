package lithium.exceptions;

public class Status469InvalidInputException extends NotRetryableErrorCodeException {
	public Status469InvalidInputException(String message) {
		super(469, message, Status469InvalidInputException.class.getCanonicalName());
	}
}
