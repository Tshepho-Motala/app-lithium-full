package lithium.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * A configuration bean that registers a custom HTTP status code on the response if the
 * exception raised is a base of {@link ErrorCodeException}.
 *
 * @see ErrorCodeException
 */
@RestControllerAdvice
@Slf4j
public class CustomHttpErrorCodeControllerAdvice {

    @ExceptionHandler(ErrorCodeException.class)
    public CustomHttpErrorCodeResponse handleErrorCodeException(ErrorCodeException ex, HttpServletResponse response) {

        if (ex.getCode() >= 500) {
            log.error("ErrorCodeException " + ex.getCode() + " " + ex.getMessage()
                    + (ex.getContext() != null ? " " + ex.getContext() : ""), ex);
        } else {
            log.debug("ErrorCodeException " + ex.getCode() + " " + ex.getMessage()
                    + (ex.getContext() != null ? " " + ex.getContext() : ""));
        }
        response.setStatus(ex.getCode());
        return CustomHttpErrorCodeResponse.from(ex);
    }
}
