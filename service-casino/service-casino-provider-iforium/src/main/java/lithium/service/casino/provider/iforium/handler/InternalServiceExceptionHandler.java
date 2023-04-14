package lithium.service.casino.provider.iforium.handler;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.controller.ServiceGameController;
import lithium.service.casino.provider.iforium.exception.ErrorParsingListGamesFileException;
import lithium.service.casino.provider.iforium.exception.InternalServerErrorException;
import lithium.service.casino.provider.iforium.exception.InvalidListGamesURLException;
import lithium.service.casino.provider.iforium.exception.NotConfiguredListGamesURLException;
import lithium.service.casino.provider.iforium.exception.SessionKeyExpiredException;
import lithium.service.casino.provider.iforium.exception.SessionTokenExpiredException;
import lithium.service.games.client.exceptions.Status501NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = ServiceGameController.class)
public class InternalServiceExceptionHandler {

    @ExceptionHandler(Status501NotImplementedException.class)
    public ResponseEntity<String> handleNotImplementedException(Status501NotImplementedException e) {
        logException(e);
        return response(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
    }

    @ExceptionHandler({SessionTokenExpiredException.class, InvalidTokenException.class, SessionKeyExpiredException.class})
    public ResponseEntity<String> handleSessionTokenExpiredException(Exception e) {
        logException(e);
        return response(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, ConstraintViolationException.class})
    public ResponseEntity<String> validateException(Exception e) {
        logException(e);
        return response(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({NotRetryableErrorCodeException.class, Status512ProviderNotConfiguredException.class})
    public ResponseEntity<IforiumHttpErrorCodeResponse> handleNotRetryableErrorCodeException(NotRetryableErrorCodeException e) {
        logException(e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, e.getCode(), e.getMessage(), e);
    }

    @ExceptionHandler(ErrorParsingListGamesFileException.class)
    public ResponseEntity<String> handleErrorParsingListGamesFileException(ErrorParsingListGamesFileException e) {
        logException(e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler({Exception.class, InternalServerErrorException.class, InvalidListGamesURLException.class,
                       NotConfiguredListGamesURLException.class})
    public ResponseEntity<String> handleException(Exception e) {
        logException(e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private static ResponseEntity<String> response(int lithiumStatusCode, String message) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, lithiumStatusCode, message);
    }

    private static ResponseEntity<String> response(HttpStatus status, String message) {
        return response(status, status.value(), message);
    }

    private static ResponseEntity<String> response(HttpStatus status, int lithiumStatusCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("lithiumStatusCode", lithiumStatusCode);
        body.put("message", message);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(JSONObject.valueToString(body));
    }

    private static ResponseEntity<IforiumHttpErrorCodeResponse> response(HttpStatus status, int lithiumStatusCode, String message, ErrorCodeException errorCodeException) {
        logException(errorCodeException);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(IforiumHttpErrorCodeResponse
                .builder()
                .lithiumStatusCode(String.valueOf(lithiumStatusCode))
                .status(lithiumStatusCode)
                .message(errorCodeException.getMessage())
                .errorCode(errorCodeException.getErrorCode())
                .build());
    }

    private static void logException(Exception e) {
        log.error("Exception is occurred, exception=" + e.getClass().getSimpleName(), e);
    }
}
