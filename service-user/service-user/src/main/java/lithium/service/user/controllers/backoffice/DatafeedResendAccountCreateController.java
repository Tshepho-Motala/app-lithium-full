package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.schema.DatafeedResendAccountCreateResponse;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class DatafeedResendAccountCreateController {

  private final PubSubUserService pubSubUserService;
  private final UserService userService;

  @GetMapping("/backoffice/datafeed-resend-account-create")
  public Response<DatafeedResendAccountCreateResponse> datafeedResendAccountCreate(@RequestParam(value = "guid") String guid, LithiumTokenUtil token) {
    User user = null;
    try {
      user = userService.findFromGuid(guid);
      pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_CREATE);
    } catch (Exception e) {
      log.error("guid: {}, stacktrace: {}", guid, ExceptionUtils.getFullStackTrace(e));
      DatafeedResendAccountCreateResponse failedResponse = pubSubUserService.getDatafeedResendAccountResponse(guid, user, ExceptionUtils.getFullStackTrace(e));
      return Response.<DatafeedResendAccountCreateResponse>builder().data(failedResponse).status(Status.INTERNAL_SERVER_ERROR).build();
    }
    DatafeedResendAccountCreateResponse successResponse = pubSubUserService.getDatafeedResendAccountResponse(guid, user, null);
    return Response.<DatafeedResendAccountCreateResponse>builder().data(successResponse).status(Status.OK).build();
  }
}
