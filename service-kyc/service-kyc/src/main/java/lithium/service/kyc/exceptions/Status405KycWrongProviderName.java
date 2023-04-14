package lithium.service.kyc.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status405KycWrongProviderName extends NotRetryableErrorCodeException {
	public Status405KycWrongProviderName() {
		super(405, "Wrong methodName provided for kyc attempt check",
				Status405KycWrongProviderName.class.getCanonicalName());
	}
}
