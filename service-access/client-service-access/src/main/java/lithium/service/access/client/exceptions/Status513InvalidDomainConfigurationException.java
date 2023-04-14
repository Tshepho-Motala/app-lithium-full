package lithium.service.access.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status513InvalidDomainConfigurationException extends NotRetryableErrorCodeException {
    public Status513InvalidDomainConfigurationException() {
        super(513, "Invalid Domain Configuration", Status513InvalidDomainConfigurationException.class.getCanonicalName());
    }
    public Status513InvalidDomainConfigurationException(String message) {
        super(513, message, Status513InvalidDomainConfigurationException.class.getCanonicalName());
    }
}
