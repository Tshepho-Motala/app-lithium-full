package lithium.exceptions;

public class Status512ProviderNotConfiguredException extends NotRetryableErrorCodeException {
    public Status512ProviderNotConfiguredException(String domain) {
        super(512, "The provider is not configured for this domain: " + domain, Status512ProviderNotConfiguredException.class.getCanonicalName());
    }
}
