package lithium.service.user.provider.threshold.services.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;

import lithium.modules.ModuleInfo;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.notifications.client.enums.Channel;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.client.objects.NotificationChannel;
import lithium.service.notifications.client.objects.NotificationType;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.user.provider.threshold.config.Properties;
import lithium.service.user.provider.threshold.config.ProviderConfig;
import lithium.service.user.provider.threshold.config.ProviderConfigService;
import lithium.service.user.provider.threshold.data.dto.NotificationDto;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.data.entities.Threshold;
import lithium.service.user.provider.threshold.data.entities.ThresholdRevision;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.enums.Type;
import lithium.service.user.provider.threshold.extremepush.ExtremePushService;
import lithium.service.user.provider.threshold.extremepush.dto.ThresholdMessage;
import lithium.service.user.provider.threshold.services.NotificationService;
import lithium.service.user.provider.threshold.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private UserService userService;
  @Autowired
  private NotificationStream notificationStream;
  @Autowired
  private Properties properties;
  @Autowired
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private ExtremePushService extremePushService;
  @Autowired
  private ProviderConfigService providerConfigService;
  @Autowired
  private ModuleInfo moduleInfo;
  @Autowired
  private ChangeLogService changeLogService;

  @Override
  public User activateNotifications(NotificationDto notificationDto, LithiumTokenUtil tokenUtil)
          throws Exception
  {
    User user = userService.findOrCreate(notificationDto.getUserGuid());
    user.setNotifications(notificationDto.isActivate());
    userService.save(user);
    String[] domainGuid = user.getGuid().split("/");
    String guidId = domainGuid[1];
        List<ChangeLogFieldChange> clfc = changeLogService.compare(user, new User(), new String[]{"notifications"});
        changeLogService.registerChangesForNotesWithFullNameAndDomain("threshold.notification", "edit", Long.parseLong(guidId), tokenUtil.guid(),
                tokenUtil, null, null, clfc, Category.ACCOUNT, SubCategory.RESPONSIBLE_GAMING, 0, notificationDto.getDomainName());
    return user;
  }

  @Override
  public User getNotificationStatus(String userGuid) {
    return userService.findByGuid(userGuid);
  }

  @Override
  public void sendMessageToPlayerInbox(Threshold threshold, User user, PlayerThresholdHistory playerThresholdHistory) {

    // Sending of notifications can be disabled globaly by use of application properties.
    if (!properties.sendNotifications()) {
      return;
    }

    // Notifications should only be sent when the user has notifications enabled
    if (!user.isNotifications()) {
      return;
    }

    ThresholdRevision thresholdRevision = threshold.getCurrent();

    List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%percentage%").value(thresholdRevision.getPercentage().toString()).build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%amount%").value(getCurrencyFormatter(cachingDomainClientService.domainLocale(thresholdRevision.getDomain().getName()))
            .format(thresholdRevision.getAmount().longValue())).build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%type%").value("ERROR_DICTIONARY.MY_ACCOUNT." +
            Type.fromName(thresholdRevision.getType().getName())).build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%granularity%").value("ERROR_DICTIONARY.MY_ACCOUNT." +
            lithium.service.accounting.enums.Granularity.fromId(thresholdRevision.getGranularity()).name()).build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%threshold_date%").value(playerThresholdHistory.getThresholdHitDate().toString()).build()); //Format date

    notificationStream.process(UserNotification.builder()
        .userGuid(user.getGuid())
        .notificationName(generateNotificationName(threshold)) // name from type table + threshold id
        .phReplacements(phReplacements)
        .cta(false)
        .build());
  }

  private NumberFormat getCurrencyFormatter(String langTag) {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag(langTag));
  }

  @Override
  public void createOrUpdateThresoldNotification(Threshold threshold) {

    List<NotificationChannel> channels = new ArrayList<>();

    channels.add(
        NotificationChannel.builder()
        .channel(
            lithium.service.notifications.client.objects.Channel.builder()
                .name(Channel.PULL.channelName())
                .build())
        .templateLang("en") //Perhaps the domain default locale?
        .templateName("")
        .forced(true)
        .build()
    );

    notificationStream.createOrUpdateNotification(
        Notification.builder()
            .domain(
                lithium.service.notifications.client.objects.Domain.builder()
                    .name(threshold.getCurrent().getDomain().getName())
                    .build()
            )
            .notificationType(
                NotificationType.builder()
                    .name(lithium.service.user.provider.threshold.data.enums.NotificationType.THRESHOLD_WARNING.name())
                    .build()
            )
            .channels(channels)
            .systemNotification(false)
            .name(generateNotificationName(threshold))
            .displayName(generateNotificationDisplayName(threshold))
            .description(generateNotificationDisplayName(threshold))
            .message(generateNotificationDescription(threshold))
            .build()
    );
  }

  // Name from type table in dot notation + . + threshold id
  private String generateNotificationName(Threshold threshold) {
    return typeNameInDotNotationLowerCase(threshold.getCurrent().getType().getName()) + "." + threshold.getId();
  }

  // LIMIT_TYPE_LOSS -> limit.type.loss
  private String typeNameInDotNotationLowerCase(String type) {
    return type.toLowerCase().replace("_", ".");
  }

  private String generateNotificationDisplayName(Threshold threshold) {
    // "Day/Week/Month/Year" + "limit type loss/win limit reached
    String s = Granularity.fromGranularity(threshold.getCurrent().getGranularity()).type().split("_")[1];
    s = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();

    return s + " " + Type.fromName(threshold.getCurrent().getType().getName()).displayName().toLowerCase() + " threshold reached";
  }

  // This is a default template showcasing the placeholders that may be used by business to configure the production ready template
  private String generateNotificationDescription(Threshold threshold) {

    String s = Granularity.fromGranularity(threshold.getCurrent().getGranularity()).type().split("_")[1].toLowerCase();

    // Your loss limit has been reached for the day. Amount: %amount%, Percentage: %percentage%, Type: %type%, Granularity: %granularity%, Threshold hit date: %threshold_date%
    return "Your %percentage%% loss limit has been reached for the " + s + ". Limit: %amount%, Early Warning Percentage: %percentage%%, Type: %type%, Granularity: %granularity%, Threshold hit date: %threshold_date%";
  }

  @Override
  public void sendMessageToExtremePush(Threshold thresholdRevision, User user, Long debitCents) {
    ProviderConfig config = providerConfigService.getConfig(moduleInfo.getModuleName(),user.getDomain().getName());
    if (config.getExtremePushAppToken() != null) {
      extremePushService.sendMessage(ThresholdMessage.builder()
          .apptoken(config.getExtremePushAppToken())
          .domainName(user.getDomain().getName())
          .user_id(user.getGuid())
          .event("LOSS_LIMIT_THRESHOLD")
          .value(debitCents.toString()).build());
    }
  }
}
