//package lithium.gateway.handler;
//
//import lithium.exceptions.Status400BadRequestException;
//import lithium.exceptions.Status413PayloadTooLargeException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.multipart.MultipartException;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//import wiremock.org.apache.commons.fileupload.FileUploadBase;
//
///**
// *
// */
//@ControllerAdvice
//@Slf4j
//public class EntityExceptionHandler extends ResponseEntityExceptionHandler {
//
//  /**
//   *
//   * @param exception
//   * @return
//   */
//  @ExceptionHandler(value = {MultipartException.class})
//  protected ResponseEntity<Object> handleMultipartException(MultipartException exception) {
//    //Lets check if our exception is a Payload Too Large exception
//    if (exception.getCause().getCause() != null && exception.getCause().getCause() instanceof FileUploadBase.FileSizeLimitExceededException) {
//      return new ResponseEntity<>(
//              new Status413PayloadTooLargeException(exception.getCause().getCause().getMessage()),
//              HttpStatus.PAYLOAD_TOO_LARGE);
//    }
//    return new ResponseEntity<>(
//            new Status400BadRequestException(exception.getCause().getCause().getMessage()),
//            HttpStatus.BAD_REQUEST);
//    }
//  }
