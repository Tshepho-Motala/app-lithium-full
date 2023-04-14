package lithium.service.kyc.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status406KycWrongMethodName extends NotRetryableErrorCodeException {
	public Status406KycWrongMethodName() {
		super(406, "Wrong providerName provided for kyc attempt check",
				Status406KycWrongMethodName.class.getCanonicalName());
	}
}
