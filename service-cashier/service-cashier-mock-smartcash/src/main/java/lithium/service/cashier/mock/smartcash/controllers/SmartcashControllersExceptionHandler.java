package lithium.service.cashier.mock.smartcash.controllers;

import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashErrorMessageException;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashStatusMessageException;
import lithium.service.cashier.mock.smartcash.data.exceptions.SmartcashStatusResponseException;
import lithium.service.cashier.processor.smartcash.data.SmartcashConnectionError;
import lithium.service.cashier.processor.smartcash.data.SmartcashPaymentResponse;
import lithium.service.cashier.processor.smartcash.data.SmartcashResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;


@RestControllerAdvice
@Slf4j
public class SmartcashControllersExceptionHandler {

    @ExceptionHandler(Exception.class)
    public SmartcashPaymentResponse handleErrorCodeException(Exception ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        return SmartcashPaymentResponse.builder()
            .status(SmartcashResponseStatus.builder().message(ex.getMessage()).build())
            .build();
    }

    @ExceptionHandler(SmartcashStatusResponseException.class)
    public SmartcashPaymentResponse handleErrorCodeException(SmartcashStatusResponseException ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        return SmartcashPaymentResponse.builder()
            .status(ex.getResponseStatus())
            .build();
    }

    @ExceptionHandler(SmartcashErrorMessageException.class)
    public SmartcashConnectionError handleErrorCodeException(SmartcashErrorMessageException ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        response.setStatus(ex.getHttpCode());
        return ex.getResponse();
    }

    @ExceptionHandler(SmartcashStatusMessageException.class)
    public SmartcashConnectionError handleErrorCodeException(SmartcashStatusMessageException ex, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + ex.getMessage(), ex);
        response.setStatus(ex.getHttpCode());
        return ex.getResponse();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public SmartcashConnectionError handleConstraintViolationException(ConstraintViolationException e, HttpServletResponse response) {
        log.error("Failed to process request. Exception: " + e.getMessage(), e);
        response.setStatus(400);
        return SmartcashConnectionError.builder().statusMessage(e.getMessage()).statusCode("ROUTER006").build();
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public SmartcashPaymentResponse handleException(MissingRequestHeaderException e) {
        log.error("Failed to process request. Exception: " + e.getMessage(), e);
        return SmartcashPaymentResponse.builder()
            .status(SmartcashResponseStatus
                .builder()
                .code("400")
                .success(false)
                .message(e.getHeaderName() + " record not found in Header")
                .responseCode("DP01100001015")
                .build())
            .build();
    }

}
