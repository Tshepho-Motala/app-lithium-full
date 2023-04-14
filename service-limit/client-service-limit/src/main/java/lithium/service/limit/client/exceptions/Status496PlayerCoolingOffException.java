package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Status496PlayerCoolingOffException extends NotRetryableErrorCodeException {
	public static final int ERROR_CODE = 496;
	public Status496PlayerCoolingOffException(String message) {
		super(496, message, Status496PlayerCoolingOffException.class.getCanonicalName());
	}

	public Status496PlayerCoolingOffException(String message, StackTraceElement[] stackTrace) {
		super(496, message, Status496PlayerCoolingOffException.class.getCanonicalName());
		if (stackTrace != null) {
			super.setStackTrace(stackTrace);
		}
	}
}
