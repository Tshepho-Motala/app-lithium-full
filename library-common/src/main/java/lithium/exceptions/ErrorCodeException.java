package lithium.exceptions;

import com.netflix.hystrix.exception.ExceptionNotWrappedByHystrix;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * The base class for all APIs that need to have a strong contract with response error codes.
 * <p>
 * Extend and throw {@code ErrorCodeException} where an API requires a clearly defined contract for error handling.
 * <p>
 * This class should never be thrown directly, rather it should be extended so that the API becomes readable
 * and generic errors are limited.
 * <p>
 * <blockquote><pre>
 *     @PostMapping("/placement")
 *     public PlacementResponse placement(@RequestBody PlacementRequest placementRequest, Principal principal)
 *             throws Status471InsufficientFundsException, Status470HashInvalidException,
 *             Status409DuplicateSubmissionException, Status500UnhandledCasinoClientException,
 *             Status422DataValidationError, Status500ProviderNotConfiguredException {
 *         return service.placement(placementRequest, principal);
 *     }
 * </pre></blockquote>
 *
 * @author Johan van den Berg
 * @see <a href="https://github.com/spring-projects/spring-retry">Spring Retry</a>
 */

public class ErrorCodeException extends HystrixBadRequestException implements ExceptionNotWrappedByHystrix {
    private static final String GENERAL_EXCEPTION_GUID = "ErrorCodeException";
    private int code = Integer.MAX_VALUE;
    private Object context;
    private String errorCode = GENERAL_EXCEPTION_GUID; //Unique string for the exception (used by feign decoder imp)

    private ErrorCodeException() {
        super("Error code exception without message");
    }

    private ErrorCodeException(String message) {
        super(message);
    }

    private ErrorCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    private ErrorCodeException(Throwable cause) {
        super("Error code exception without message", cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorCodeException(int code, String message, final String globallyUniqueErrorIdentifier) {
        super(message);
        this.code = code;
        errorCode = globallyUniqueErrorIdentifier;
    }

    public ErrorCodeException(int code, String message, Object context, final String globallyUniqueErrorIdentifier) {
        super(message);
        this.code = code;
        this.context = context;
        errorCode = globallyUniqueErrorIdentifier;
    }

    public ErrorCodeException(int code, String message, Throwable cause, final String globallyUniqueErrorIdentifier) {
        super(message, cause);
        this.code = code;
        errorCode = globallyUniqueErrorIdentifier;
    }

    public ErrorCodeException(int code, String message, Throwable cause, Object context, final String globallyUniqueErrorIdentifier) {
        super(message, cause);
        this.code = code;
        this.context = context;
        errorCode = globallyUniqueErrorIdentifier;
    }

    public int getCode() {
        return code;
    }
    public Object getContext() { return context; }
    public ErrorCodeException setContext(Object context) { this.context = context; return this; }

    // While having the code in the message always seems like a good idea, it does break
    // agreement with frontend.
    //    @Override
    //    public String getMessage() {
    //        return getCode() + " : " + super.getMessage();
    //    }

    @Override
    public String toString() {
        return "ErrorCodeException [" +
                "code=" + code +
                ", msg=" + getMessage() +
                ", context=" + context +
                ", errorCode='" + errorCode + '\'' +
                ']';
    }
}
