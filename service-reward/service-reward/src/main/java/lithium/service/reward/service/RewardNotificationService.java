package lithium.service.reward.service;

import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.notifications.client.objects.Channel;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.client.objects.NotificationChannel;
import lithium.service.notifications.client.objects.NotificationType;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.enums.NotificationTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class RewardNotificationService {
    private final NotificationStream notificationStream;
    private final CachingDomainClientService cachingDomainClientService;

    public void sendPendingRewardTypeNotification(PlayerRewardTypeHistory history) {
        String domainName = history.getPlayerRewardHistory().getPlayer().domainName();
        boolean inboxEnabled = notificationInboxEnabled(domainName);

        if (inboxEnabled) {
            Map<String, String> metaData = new HashMap<>();
            metaData.put("playerRewardComponentHistoryId", history.getId().toString());
            metaData.put("requiresAcceptance", "true");
            RewardRevisionType rewardRevisionType = history.getRewardRevisionType();

            List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
            notificationStream.process(UserNotification.builder()
                    .userGuid(history.getPlayerRewardHistory().getPlayer().guid())
                    .notificationName(getNotificationName(rewardRevisionType))
                    .phReplacements(phReplacements)
                    .cta(true)
                    .metaData(metaData)
                    .build());
        } else {
            log.warn("Domain setting {} has not been enabled/configured on domain {},the acceptance notification for reward component {} will now be skipped",
                    DomainSettings.SEND_REWARD_NOTIFICATION_TO_PLAYER_INBOX.key(), domainName, history.getRewardRevisionType().getRewardType().getName());
        }
    }

    public String getNotificationName(RewardRevisionType rewardRevisionType) {
        //The notification name will be in this form (reward.accept.freespin.1)
        return MessageFormat.format("reward.accept.{0}.{1}", rewardRevisionType.getRewardType().getName(), rewardRevisionType.getId());
    }


    public void registerRewardTypeNotification(RewardRevisionType rewardRevisionType) {
        if (rewardRevisionType.isInstant()) {
            return;
        }

        NotificationChannel notificationChannel = NotificationChannel
                .builder()
                .channel(
                        Channel.builder()
                                .name(lithium.service.notifications.client.enums.Channel.PULL.channelName())
                                .build())
                .templateLang("en")
                .templateName("")
                .forced(true)
                .build();
        List<NotificationChannel> channels = new ArrayList<>();
        channels.add(notificationChannel);

        Notification notification = Notification.builder()
                .systemNotification(false)
                .name(getNotificationName(rewardRevisionType))
                .displayName(rewardRevisionType.getRewardType().getName())
                .description(rewardRevisionType.toString())
                .message(rewardRevisionType.getNotificationMessage())
                .domain(lithium.service.notifications.client.objects.Domain.builder().name(rewardRevisionType.getRewardRevision().getReward().getDomain().getName()).build())
                .channels(channels)
                .notificationType(NotificationType.builder()
                        .name(NotificationTypes.REWARD.getType())
                        .build())
                .build();

        notificationStream.createOrUpdateNotification(notification);
    }

    public boolean notificationInboxEnabled(String domainName) {
        DomainSettings setting = DomainSettings.SEND_REWARD_NOTIFICATION_TO_PLAYER_INBOX;
        String shouldSend = setting.defaultValue();

        try {
            shouldSend = Optional.ofNullable(cachingDomainClientService.getDomainSetting(domainName, setting))
                    .orElse(shouldSend);
        } catch (Exception e) {
            log.error("Failed to get the setting {} for domain {}, going to use the default value of {}", domainName,
                    setting.key(), shouldSend);
        }

        return Boolean.parseBoolean(shouldSend);
    }

    public void registerNotificationTypes() {
        notificationStream.registerNotificationType(NotificationTypes.REWARD.getType());
    }
}
