package lithium.service.document.provider.api.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status540ProviderNotConfiguredException extends NotRetryableErrorCodeException {
    public Status540ProviderNotConfiguredException(String message) {
        super(540, message, Status540ProviderNotConfiguredException.class.getCanonicalName());
    }
}
