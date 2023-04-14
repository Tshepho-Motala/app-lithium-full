package lithium.exceptions;

public class Status460LoginRestrictedException extends NotRetryableErrorCodeException {
	public static final int ERROR_CODE = 460;
	public Status460LoginRestrictedException(String message) {
		super(460, message, Status460LoginRestrictedException.class.getCanonicalName());
	}

	public Status460LoginRestrictedException(String message, StackTraceElement[] stackTrace) {
		super(460, message, Status460LoginRestrictedException.class.getCanonicalName());
		if (stackTrace != null) {
			super.setStackTrace(stackTrace);
		}
	}
}
