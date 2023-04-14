package lithium.jpa.exceptions;

/**
 * The existing jpa CannotAcquireLockException is a runtime exception. This means we are not forced to handle it.
 * This wrapper exception is thus used to force callers to catch and handle the exception, or rethrow it if needed.
 */
public class CannotAcquireLockException extends Exception {

    public CannotAcquireLockException(String message, org.springframework.dao.CannotAcquireLockException cause) {
        super(message, cause);
    }

}
