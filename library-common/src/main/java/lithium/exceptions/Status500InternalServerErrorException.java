package lithium.exceptions;

/**
 * We want a circuit break here. This should not be a normal scenario. Throw too many of these
 * and the system will remove the service from active duty. In order to achieve that, we cannot
 * inherit from ErrorCodeException, as this is explicitly excluded from Hystrix circuit breaker.
 */
public class Status500InternalServerErrorException extends Exception {

    public static final int CODE = 500;

    public Status500InternalServerErrorException(String message) {
        super(message);
    }

    public Status500InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public Status500InternalServerErrorException(String service, String message) {
        super(service + " " + message);
    }

    public Status500InternalServerErrorException(String service, String message, Throwable cause) {
        super(service + " " + message, cause);
    }

    public Status500InternalServerErrorException(String message, StackTraceElement[] stackTrace) {
        super((message));
        if (stackTrace != null) {
            super.setStackTrace(stackTrace);
        }
    }
}
