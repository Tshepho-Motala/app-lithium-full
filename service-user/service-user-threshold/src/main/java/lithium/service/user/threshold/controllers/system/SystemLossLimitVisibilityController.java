package lithium.service.user.threshold.controllers.system;

import lithium.service.user.threshold.client.enums.LossLimitVisibilityMessageType;
import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SystemLossLimitVisibilityController implements SystemLossLimitVisibility {

  @Autowired
  private NotificationService notificationService;

  @Override
  public void sendLossLimitVisibilityNotification(String playerGuid, String messageType) {
    ProcessingContext processingContext = new ProcessingContext();
    User user = new User();
    user.setGuid(playerGuid);
    processingContext.setUser(user);
    processingContext.setMessageType(LossLimitVisibilityMessageType.valueOf(messageType));
    notificationService.sendLossLimitVisibilityToPlayerInbox(processingContext);
  }
}
