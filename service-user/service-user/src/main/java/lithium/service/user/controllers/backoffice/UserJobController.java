package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.data.objects.UserJobRequest;
import lithium.service.user.data.entities.UserJob;
import lithium.service.user.services.UserJobService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController(value = "/backoffice/user-jobs")
@Validated
public class UserJobController {

  @Autowired
  UserJobService userJobService;


  @PostMapping
  public Response<UserJob> create(@Valid @RequestBody UserJobRequest request, LithiumTokenUtil util) throws Exception {
    return Response.<UserJob>builder()
            .status(Status.OK)
            .data(userJobService.findOrCreate(request, util.id()))
            .build();
  }

  @ExceptionHandler({Exception.class})
  public Response<?> exceptionHandler(Exception exception) {
    return Response.builder().message(exception.getMessage()).status(Status.INTERNAL_SERVER_ERROR).build();
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public Response<?> exceptionHandler(MethodArgumentNotValidException exception) {
    return Response.builder().data(
        exception.getBindingResult().getFieldErrors()
            .stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage))
    ).status(Status.BAD_REQUEST).build();
  }
}
