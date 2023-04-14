package lithium.service.cashier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(value = { HttpServerErrorException.class })
	protected ResponseEntity<Object> handleServerError(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = "HttpServerErrorException :: "+ex.getMessage();
		log.error(bodyOfResponse, ex);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(value = { HttpClientErrorException.class })
	protected ResponseEntity<Object> handleClientError(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = "HttpClientErrorException :: "+ex.getMessage();
		log.error(bodyOfResponse, ex);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
}