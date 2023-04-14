package lithium.service.shards.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status404ShardNotFoundException extends NotRetryableErrorCodeException {
	public Status404ShardNotFoundException(final String message) {
		super(404, message, Status404ShardNotFoundException.class.getCanonicalName());
	}
}
