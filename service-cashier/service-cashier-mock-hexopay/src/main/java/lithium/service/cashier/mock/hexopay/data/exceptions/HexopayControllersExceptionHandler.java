package lithium.service.cashier.mock.hexopay.data.exceptions;

import lithium.service.cashier.processor.hexopay.api.gateway.ErrorResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.data.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class HexopayControllersExceptionHandler {

    @ExceptionHandler(HexopayGatewayMockException.class)
    public ErrorResponse handleErrorCodeException(HexopayGatewayMockException ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .response(ErrorDetails.builder().message(ex.getMessage()).build())
                .build();
        response.setStatus(ex.getHttpCode());
        return errorResponse;
    }

    @ExceptionHandler(HexopayPageMockException.class)
    public ErrorDetails handleErrorCodeException(HexopayPageMockException ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        ErrorDetails errorDetails = ErrorDetails.builder().message(ex.getMessage()).build();
        response.setStatus(ex.getHttpCode());
        return errorDetails;
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleErrorCodeException(Exception ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .response(ErrorDetails.builder().message(ex.getMessage()).build())
                .build();
        response.setStatus(500);
        return errorResponse;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
