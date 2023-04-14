package lithium.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * An abstract base for API exceptions so that the exclude from {@code @Retryable} can be used to specify
 * a single exclusion for all exceptions that should not cause a retry.
 * <p>
 * The {@code NotRetryableErrorCodeException} class extends the {@link ErrorCodeException} class
 * with the same prototype and no change in function.
 * <p>
 * <blockquote><pre>
 *     @Retryable(exclude = { NotRetryableErrorCodeException.class })
 * </pre></blockquote>
 *
 * @author Johan van den Berg
 * @see lithium.exceptions.ErrorCodeException
 * @see <a href="https://github.com/spring-projects/spring-retry">Spring Retry</a>
 */

public abstract class NotRetryableErrorCodeException extends ErrorCodeException {

    public NotRetryableErrorCodeException(int code, String message, String globallyUniqueErrorIdentifier) {
        super(code, message, globallyUniqueErrorIdentifier);
    }

    public NotRetryableErrorCodeException(int code, String message, Object context, String globallyUniqueErrorIdentifier) {
        super(code, message, context, globallyUniqueErrorIdentifier);
    }

    public NotRetryableErrorCodeException(int code, String message, Throwable cause, String globallyUniqueErrorIdentifier) {
        super(code, message, cause, globallyUniqueErrorIdentifier);
    }

    public NotRetryableErrorCodeException(int code, String message, Throwable cause, Object context, String globallyUniqueErrorIdentifier) {
        super(code, message, cause, context, globallyUniqueErrorIdentifier);
    }
}
