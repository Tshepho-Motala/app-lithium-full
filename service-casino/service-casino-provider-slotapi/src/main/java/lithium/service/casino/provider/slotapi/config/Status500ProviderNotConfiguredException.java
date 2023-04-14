package lithium.service.casino.provider.slotapi.config;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status500ProviderNotConfiguredException extends NotRetryableErrorCodeException {
    public Status500ProviderNotConfiguredException() {
        super(500, "The provider is not configured for this domain", Status500ProviderNotConfiguredException.class.getCanonicalName());
    }
}
