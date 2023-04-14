package lithium.service.domain.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status474DomainProviderDisabledException extends NotRetryableErrorCodeException {
	public Status474DomainProviderDisabledException(String message) {
		super(474, message, Status474DomainProviderDisabledException.class.getCanonicalName());
	}
}
