package lithium.service.geo.neustar.exception;

import lithium.service.geo.neustar.objects.GDSError;
import lithium.service.geo.neustar.response.GeolocationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NeustarCompatibleGeoControllerAdvice {

    @ExceptionHandler(InvalidIPV4AddressException.class)
    @ResponseStatus
    public ResponseEntity<GeolocationResponse> handleInvalidIPException(InvalidIPV4AddressException invalidIPV4AddressException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GeolocationResponse.builder()
                                         .gdsError(GDSError.builder()
                                                                 .httpStatus(HttpStatus.BAD_REQUEST.toString())
                                                                 .message(invalidIPV4AddressException.getMessage())
                                                                 .build())
                                         .build()
                     );
    }

    @ExceptionHandler(UnauthorizedAPIKeyException.class)
    @ResponseStatus
    public ResponseEntity<GeolocationResponse> handleUnauthorizedAPIKeyException(UnauthorizedAPIKeyException unauthorizedAPIKeyException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(GeolocationResponse.builder()
                                         .gdsError(GDSError.builder()
                                                                 .httpStatus(HttpStatus.UNAUTHORIZED.toString())
                                                                 .message(unauthorizedAPIKeyException.getMessage())
                                                                 .build())
                                         .build()
                     );
    }

    @ExceptionHandler(IPV4AddressNotFoundException.class)
    @ResponseStatus
    public ResponseEntity<GeolocationResponse> handleIPV4AddressNotFoundException(IPV4AddressNotFoundException iPV4AddressNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(GeolocationResponse.builder()
                                         .gdsError(GDSError.builder()
                                                           .httpStatus(HttpStatus.NOT_FOUND.toString())
                                                           .message(iPV4AddressNotFoundException.getMessage())
                                                           .build())
                                         .build()
                     );
    }

}
