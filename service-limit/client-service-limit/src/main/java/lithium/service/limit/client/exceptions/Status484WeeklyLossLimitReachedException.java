package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status484WeeklyLossLimitReachedException extends NotRetryableErrorCodeException {
	public static final int CODE = 484;

	public Status484WeeklyLossLimitReachedException(String message) {
		super(CODE, message, Status484WeeklyLossLimitReachedException.class.getCanonicalName());
	}
}
