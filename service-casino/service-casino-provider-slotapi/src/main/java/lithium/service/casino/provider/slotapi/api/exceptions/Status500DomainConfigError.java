package lithium.service.casino.provider.slotapi.api.exceptions;

import lithium.exceptions.ErrorCodeException;

public class Status500DomainConfigError extends ErrorCodeException {
    public Status500DomainConfigError(Throwable cause) {
        super(500, "Could not retrieve domain", cause, Status500DomainConfigError.class.getCanonicalName());
    }
}
