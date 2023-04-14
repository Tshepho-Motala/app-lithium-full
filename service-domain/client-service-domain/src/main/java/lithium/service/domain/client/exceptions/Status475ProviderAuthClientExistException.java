package lithium.service.domain.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status475ProviderAuthClientExistException extends NotRetryableErrorCodeException {
	public Status475ProviderAuthClientExistException(String message) {
		super(475, message, Status475ProviderAuthClientExistException.class.getCanonicalName());
	}
}
