package lithium.service.exception;

import lithium.exceptions.ErrorCodeException;

public class Status511UpstreamServiceUnavailableException extends ErrorCodeException {
    public static final int CODE = 511;
    public Status511UpstreamServiceUnavailableException(String message) {
        super(CODE, "Upstream service error: " + message, Status511UpstreamServiceUnavailableException.class.getCanonicalName());
    }
}
