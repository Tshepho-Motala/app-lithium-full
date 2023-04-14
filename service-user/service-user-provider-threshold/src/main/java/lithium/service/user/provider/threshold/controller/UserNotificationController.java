package lithium.service.user.provider.threshold.controller;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.provider.threshold.data.dto.NotificationDto;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.services.NotificationService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/user-notification")
public class UserNotificationController {

  @Autowired
  private NotificationService notificationService;
  @Autowired
  private ChangeLogService changeLogService;

  @PostMapping("/activate")
  public Response<User> activateNotifications(@RequestBody NotificationDto notificationDto, LithiumTokenUtil tokenUtil)
  throws Exception
  {
    User user = notificationService.activateNotifications(notificationDto, tokenUtil);
    return Response.<User>builder().data(user).status(Status.OK).build();
  }
  @GetMapping("/status/p")
  public Response<User> getNotificationStatus(@RequestParam  String userGuid)
  throws Status500InternalServerErrorException
  {
    User user = notificationService.getNotificationStatus(userGuid);
    return Response.<User>builder().data(user).status(Status.OK).build();
  }

}
