package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status520KycProviderEndpointException extends NotRetryableErrorCodeException {
    public Status520KycProviderEndpointException(String message) {
        super(520, message, Status520KycProviderEndpointException.class.getCanonicalName());
    }
}
