package lithium.service.domain.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status473DomainBettingDisabledException extends NotRetryableErrorCodeException {
	public Status473DomainBettingDisabledException(String message) {
		super(473, message, Status473DomainBettingDisabledException.class.getCanonicalName());
	}
}
