package lithium.service.cashier.processor.mvend;

import lithium.exceptions.ErrorCodeException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.schema.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class MvendExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Use the current exception purely for the ability to see all usages within the IDE.
        Status901InvalidOrMissingParameters newException = new Status901InvalidOrMissingParameters(
                "Missing request parameter: " + ex.getParameterName());
        log.warn("Exception on request: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
        return new ResponseEntity<Object>(
                new Response(newException.getCode(), newException.getMessage()),
                null, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Use the current exception purely for the ability to see all usages within the IDE.
        Status901InvalidOrMissingParameters newException = new Status901InvalidOrMissingParameters(ex.getMessage());
        log.warn("Exception on request: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
        return new ResponseEntity<Object>(
                new Response(newException.getCode(), newException.getMessage()),
                null, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("Exception on request: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
        return new ResponseEntity<Object>(
                new Response(909, ex.getMessage()), null, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleErrorCodeException(ErrorCodeException ex) {
        log.warn("Exception on request: (" + ex.getCode() + ") " + ex.getMessage());
        return new ResponseEntity<Object>(
                new Response(ex.getCode(), ex.getMessage()), null, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(
            Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("Exception on request: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
        return new ResponseEntity<Object>(
                new Response(200, ex.getMessage()), null, HttpStatus.OK);
    }

}