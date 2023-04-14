package lithium.service.stats.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status513StatsServiceUnavailableException extends NotRetryableErrorCodeException {
	public Status513StatsServiceUnavailableException(String message) {
		super(513, message, Status513StatsServiceUnavailableException.class.getCanonicalName());
	}
}
