package lithium.exceptions;

public class Status408DomainMethodDisabledException  extends NotRetryableErrorCodeException  {
	public static final int ERROR_CODE = 408;

	public Status408DomainMethodDisabledException(String message) {
		super(ERROR_CODE, message, Status408DomainMethodDisabledException.class.getCanonicalName());
	}

}
