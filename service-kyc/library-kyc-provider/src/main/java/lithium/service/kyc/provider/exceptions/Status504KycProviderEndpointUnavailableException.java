package lithium.service.kyc.provider.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status504KycProviderEndpointUnavailableException extends NotRetryableErrorCodeException {
    public Status504KycProviderEndpointUnavailableException(String message) {
        super(504, message, Status504KycProviderEndpointUnavailableException.class.getCanonicalName());
    }
}
