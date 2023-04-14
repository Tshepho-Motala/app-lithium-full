package lithium.service.promo.controllers;

import lithium.service.promo.dtos.ValidationErrorResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
abstract public class ValidationController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException e, WebRequest webRequest) {

        ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.builder()
                .message("Failed to process invalid data")
                .date(DateTime.now(DateTimeZone.UTC).toDate())
                .build();

        for(FieldError fieldError: e.getBindingResult().getFieldErrors()) {
            validationErrorResponse.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(validationErrorResponse);
    }
}
