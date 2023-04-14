package lithium.ui.network.admin.handler;

import lithium.exceptions.Status413PayloadTooLargeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 */
@ControllerAdvice
@Slf4j
public class EntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   *
   * @param exception
   * @return
   */
  @ExceptionHandler(value = {MultipartException.class})
  protected ResponseEntity<Object> handleMultipartException(MultipartException exception) {
    //Lets check if our exception is a Payload Too Large exception
    if (exception.getCause() != null
        && exception.getCause() instanceof IllegalStateException) {

      return new ResponseEntity<>(
          new Status413PayloadTooLargeException(exception.getCause().getMessage()),
          HttpStatus.PAYLOAD_TOO_LARGE);
    }

    throw exception;
  }
}
