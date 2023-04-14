package lithium.service.user.controllers.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.user.client.UserStatusClient;
import lithium.service.user.services.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RestController
@RequestMapping("/system/user-status")
public class SystemUserStatusController implements UserStatusClient {

  @Autowired
  private StatusService statusService;

  @Autowired
  private ObjectMapper mapper;

  @RequestMapping(value = "/get-all-statuses", method = RequestMethod.POST)
  public Response<List<lithium.service.user.client.objects.Status>> getAllUserStatuses() {

    List<lithium.service.user.client.objects.Status> statuses = StreamSupport.stream(statusService.findAllNotDeletedStatuses().spliterator(), false)
        .map(status -> mapper.convertValue(status, lithium.service.user.client.objects.Status.class))
        .collect(Collectors.toList());

    return Response.<List<lithium.service.user.client.objects.Status>>builder()
        .status(Response.Status.OK)
        .data(statuses)
        .build();
  }

  @RequestMapping(value = "/get-all-status-reasons", method = RequestMethod.POST)
  public Response<List<lithium.service.user.client.objects.StatusReason>> getAllStatusReasons() {

    List<lithium.service.user.client.objects.StatusReason> reasons = StreamSupport.stream(statusService.findAllStatusReasons().spliterator(),false)
            .map(reason -> mapper.convertValue(reason, lithium.service.user.client.objects.StatusReason.class))
                    .collect(Collectors.toList());

    return Response.<List<lithium.service.user.client.objects.StatusReason>>builder()
        .status(Response.Status.OK)
        .data(reasons)
        .build();
  }
}
