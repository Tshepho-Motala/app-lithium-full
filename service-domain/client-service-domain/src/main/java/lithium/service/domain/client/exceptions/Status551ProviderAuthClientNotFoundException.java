package lithium.service.domain.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status551ProviderAuthClientNotFoundException extends NotRetryableErrorCodeException {
    public Status551ProviderAuthClientNotFoundException(String message) {
        super(551, message, Status551ProviderAuthClientNotFoundException.class.getCanonicalName());
    }
}
