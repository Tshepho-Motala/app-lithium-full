package lithium.service.mail.client.exceptions;

import lithium.exceptions.NotRetryableErrorCodeException;

public class Status403InvalidProviderCredentials extends NotRetryableErrorCodeException {
    public Status403InvalidProviderCredentials(String message) {
        super(403, message, Status403InvalidProviderCredentials.class.getCanonicalName());
    }
}
