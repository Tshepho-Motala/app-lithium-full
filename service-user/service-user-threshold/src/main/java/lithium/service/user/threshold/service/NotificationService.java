package lithium.service.user.threshold.service;

import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.entities.Threshold;

public interface NotificationService {

  void sendToPlayerInbox(ProcessingContext context);

  void sendToExternal(ProcessingContext context);

  void sendLossLimitVisibilityToPlayerInbox(ProcessingContext context);

  void createOrUpdateThresholdNotification(Threshold threshold);

  void registerAndCreateNotifications();
}
