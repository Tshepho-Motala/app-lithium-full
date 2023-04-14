package lithium.service.datafeed.provider.google.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status512ProviderNotConfiguredException extends NotRetryableErrorCodeException {
    public Status512ProviderNotConfiguredException(String providerName) {
        super(512, "The provider " + providerName + " is not configured for use yet. Please do not resubmit.", Status512ProviderNotConfiguredException.class.getCanonicalName());
    }
}