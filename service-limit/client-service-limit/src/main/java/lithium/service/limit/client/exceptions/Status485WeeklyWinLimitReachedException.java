package lithium.service.limit.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status485WeeklyWinLimitReachedException extends NotRetryableErrorCodeException {
	public static final int CODE = 485;

	public Status485WeeklyWinLimitReachedException(String message) {
		super(CODE, message, Status485WeeklyWinLimitReachedException.class.getCanonicalName());
	}
}
