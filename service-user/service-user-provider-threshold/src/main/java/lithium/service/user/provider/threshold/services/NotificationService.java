package lithium.service.user.provider.threshold.services;


import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.provider.threshold.data.dto.NotificationDto;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.tokens.LithiumTokenUtil;

public  interface NotificationService {

  User activateNotifications(NotificationDto notificationDto, LithiumTokenUtil tokenUtil)
          throws Exception;
  User getNotificationStatus(String userGuid)
  throws Status500InternalServerErrorException;
  void sendMessageToPlayerInbox(Threshold thresholdRevision,User user, PlayerThresholdHistory playerThresholdHistory);
  void createOrUpdateThresoldNotification(Threshold threshold);
  void sendMessageToExtremePush(Threshold thresholdRevision,User user, Long debitCents);
}
