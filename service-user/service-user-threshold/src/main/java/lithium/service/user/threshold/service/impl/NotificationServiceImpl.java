package lithium.service.user.threshold.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lithium.math.CurrencyAmount;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.notifications.client.enums.Channel;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.Notification;
import lithium.service.notifications.client.objects.NotificationChannel;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.client.enums.LossLimitVisibilityMessageType;
import lithium.service.user.threshold.config.ThresholdProperties;
import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.dto.ExternalServiceMessage;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.service.user.threshold.data.enums.NotificationType;
import lithium.service.user.threshold.service.ExtremePushService;
import lithium.service.user.threshold.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  ThresholdProperties thresholdProperties;
  @Autowired
  CachingDomainClientService cachingDomainClientService;
  @Autowired
  NotificationStream notificationStream;
  @Autowired
  ExtremePushService extremePushService;
  @Autowired
  LimitInternalSystemService limitInternalSystemService;

  @Autowired
  MessageSource messageSource;

  @Override
  public void sendToPlayerInbox(ProcessingContext context) {
    // Sending of notifications can be disabled globally by use of application properties.
    if (!thresholdProperties.sendNotifications()) {
      log.debug("Threshold notifications switched off globally.");
      return;
    }

    // Notifications should only be sent when the user has notifications enabled
    if (!context.getUser().isNotifications()) {
      log.debug("Threshold notifications for player ({}) disabled.", context.getUser().getGuid());
      return;
    }
    //TODO: This is a quickfix, needs to be configurable
    if (!"TYPE_LOSS_LIMIT".equalsIgnoreCase(context.getThreshold().getType().getName())) {
      return;
    }

    LossLimitsVisibility losslimitsvisibility = limitInternalSystemService.getLossLimitVisibility(context.getUser().getGuid())
        .getLossLimitsVisibility();
    if (losslimitsvisibility == LossLimitsVisibility.OFF) {
      log.warn("Loss limit visibility switched off, no notifications for player ({}) will be sent.", context.getUser().getGuid());
    } else if (losslimitsvisibility == LossLimitsVisibility.DISABLED) {
      log.info("Enabling loss limit visibility for player ({}) and sending inbox notification.", context.getUser().getGuid());
      //TODO: add changelog here.
      limitInternalSystemService.setLossLimitVisibility(context.getUser().getGuid(), LossLimitsVisibility.ENABLED);
    } else if (losslimitsvisibility == LossLimitsVisibility.ENABLED) {
      // This was requested by PO. Only send threshold message if the loss limit visibility is enabled.
      sendThresholdReachedToInbox(context);
    }
  }

  private void sendThresholdReachedToInbox(ProcessingContext context) {
    ThresholdRevision thresholdRevision = context.getThreshold().getCurrent();
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(context.getDomain().getName());
    CurrencyAmount amount = getAmount(context);
    String localeStr = domain.getDefaultLocale();
    localeStr = localeStr.replace("-", "_");
    Locale locale = LocaleUtils.toLocale(localeStr);

    List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
    if (!ObjectUtils.isEmpty(thresholdRevision.getPercentage())) {
      phReplacements.add(
          InboxMessagePlaceholderReplacement.builder().key("%percentage%").value(thresholdRevision.getPercentage().toString() + "%").build());
    }
    if (!ObjectUtils.isEmpty(amount)) {
      phReplacements.add(InboxMessagePlaceholderReplacement.builder()
          .key("%amount%")
          .value(CurrencyAmount.formatUsingLocale(amount.toCents(), locale, domain.getCurrencySymbol(), domain.getCurrency()))
          .build());
    }
    phReplacements.add(InboxMessagePlaceholderReplacement.builder()
        .key("%type%")
        .value("ERROR_DICTIONARY.THRESHOLDS." + EType.fromName(context.getThreshold().getType().getName()))
        .build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder()
        .key("%granularity%")
        .value("ERROR_DICTIONARY.THRESHOLDS." + context.getThreshold().getGranularity().name())
        .build());
    phReplacements.add(
        InboxMessagePlaceholderReplacement.builder().key("%limit%").value(context.getLimit().getLimitAmount().toPlainString()).build());
    phReplacements.add(InboxMessagePlaceholderReplacement.builder()
        .key("%threshold_date%")
        .value(context.getPlayerThresholdHistory().getThresholdHitDate().toString())
        .build());

    notificationStream.process(UserNotification.builder()
        .userGuid(context.getUser().getGuid())
        .notificationName(notificationName(context.getThreshold())) // name from type table + threshold id
        .phReplacements(phReplacements)
        .cta(false)
        .build());
    context.setMessageType(LossLimitVisibilityMessageType.THRESHOLD_REACHED);
    sendLossLimitVisibilityToPlayerInbox(context);
  }

  @Override
  public void sendToExternal(ProcessingContext context) {
    CurrencyAmount amount = getAmount(context);
    extremePushService.sendMessage(ExternalServiceMessage.builder()
        .domainName(context.getUser().getDomain().getName())
        .userId(context.getUser().getGuid())
        .event("LOSS_LIMIT_THRESHOLD")
        .value((!ObjectUtils.isEmpty(amount)) ? amount.toString() : "")
        .build());
  }

  private CurrencyAmount getAmount(ProcessingContext context) {
    switch (context.getThreshold().getGranularity()) {
      case GRANULARITY_DAY -> {
        return CurrencyAmount.fromAmount(context.getPlayerThresholdHistory().getDailyLossLimitUsed());
      }
      case GRANULARITY_WEEK -> {
        return CurrencyAmount.fromAmount(context.getPlayerThresholdHistory().getWeeklyLossLimitUsed());
      }
      case GRANULARITY_MONTH -> {
        return CurrencyAmount.fromAmount(context.getPlayerThresholdHistory().getMonthlyLossLimitUsed());
      }
      default -> {
        log.error("Unknown granularity received. {}", context);
        return null;
      }
    }
  }

  @Override
  public void sendLossLimitVisibilityToPlayerInbox(ProcessingContext context) {
    String guid = context.getUser().getGuid();
    LossLimitVisibilityMessageType messageType = context.getMessageType();
    String visibilityReason = messageSource.getMessage(
        "SERVICE_USER_THRESHOLD.NOTIFICATIONS.LOSS_LIMIT_VISIBILITY.VISIBILITY_REASON." + messageType.name(),
        new Object[] {new lithium.service.translate.client.objects.Domain(guid.split("/")[0])}, "Please contact customer support.",
        LocaleContextHolder.getLocale());

    List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
    phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%VISIBILITY_REASON%").value(visibilityReason).build());

    UserNotification userNotification = UserNotification.builder()
        .userGuid(guid)
        .notificationName(typeNameInDotNotationLowerCase(NotificationType.LOSS_LIMIT_VISIBILITY.name()))
        .phReplacements(phReplacements)
        .cta(false)
        .build();
    notificationStream.process(userNotification);
  }

  @Override
  public void createOrUpdateThresholdNotification(Threshold threshold) {
    //TODO: This is a quickfix, needs to be configurable
    if (!"TYPE_LOSS_LIMIT".equalsIgnoreCase(threshold.getType().getName())) {
      return;
    }

    List<NotificationChannel> channels = new ArrayList<>();

    channels.add(NotificationChannel.builder()
        .channel(lithium.service.notifications.client.objects.Channel.builder().name(Channel.PULL.channelName()).build())
        .templateLang("en") //Perhaps the domain default locale?
        .templateName("")
        .forced(true)
        .build());

    String msgKey = "SERVICE_USER_THRESHOLD.NOTIFICATIONS.THRESHOLD_WARNING.";
    msgKey += threshold.getType().getName() + ".";
    msgKey += threshold.getGranularity().type() + ".";
    notificationStream.createOrUpdateNotification(Notification.builder()
        .domain(lithium.service.notifications.client.objects.Domain.builder().name(threshold.getDomain().getName()).build())
        .notificationType(
            lithium.service.notifications.client.objects.NotificationType.builder().name(NotificationType.THRESHOLD_WARNING.name()).build())
        .channels(channels)
        .systemNotification(false)
        .name(notificationName(threshold))
        .displayName(messageSource.getMessage(msgKey + "DISPLAY_NAME",
            new Object[] {new lithium.service.translate.client.objects.Domain(threshold.getDomain().getName())}, "Threshold Warning.",
            LocaleContextHolder.getLocale()))
        .description(messageSource.getMessage(msgKey + "DESCRIPTION",
            new Object[] {new lithium.service.translate.client.objects.Domain(threshold.getDomain().getName())}, "Threshold Warning.",
            LocaleContextHolder.getLocale()))
        .message(messageSource.getMessage(msgKey + "MESSAGE",
            new Object[] {new lithium.service.translate.client.objects.Domain(threshold.getDomain().getName())}, "Threshold Warning.",
            LocaleContextHolder.getLocale()))
        .build());
  }

  @Override
  public void registerAndCreateNotifications() {
    notificationStream.registerNotificationType(NotificationType.THRESHOLD_WARNING.name());
    notificationStream.registerNotificationType(NotificationType.LOSS_LIMIT_VISIBILITY.name());

    //    String domainName=playerGuid.split("/")[0];
    List<Domain> domains = null;
    try {
      domains = cachingDomainClientService.getDomainClient().findAllPlayerDomains().getData();
    } catch (Status550ServiceDomainClientException e) {
      log.error("Failed to retrieve list of player domains, cannot process inactive session timeouts | " + e.getMessage(), e);
    } finally {
      if (domains == null) {
        log.error("COULD NOT RETRIEVE PLAYER DOMAINS, NOTIFICATION TYPES MIGHT BE MISSING!");
        return;
      }
    }

    List<NotificationChannel> channels = new ArrayList<>();

    channels.add(NotificationChannel.builder()
        .channel(lithium.service.notifications.client.objects.Channel.builder().name(Channel.PULL.channelName()).build())
        .templateLang("en") //Perhaps the domain default locale?
        .templateName("")
        .forced(true)
        .build());

    for (Domain domain: domains) {
      notificationStream.createOrUpdateNotification(Notification.builder()
          .domain(lithium.service.notifications.client.objects.Domain.builder().name(domain.getName()).build())
          .notificationType(
              lithium.service.notifications.client.objects.NotificationType.builder().name(NotificationType.LOSS_LIMIT_VISIBILITY.name()).build())
          .channels(channels)
          .systemNotification(false)
          .name(typeNameInDotNotationLowerCase(NotificationType.LOSS_LIMIT_VISIBILITY.name()))
          .displayName(messageSource.getMessage("SERVICE_USER_THRESHOLD.NOTIFICATIONS.LOSS_LIMIT_VISIBILITY.DISPLAY_NAME",
              new Object[] {new lithium.service.translate.client.objects.Domain(domain.getName())}, "Loss Limit Visibility.",
              LocaleContextHolder.getLocale()))
          .description(messageSource.getMessage("SERVICE_USER_THRESHOLD.NOTIFICATIONS.LOSS_LIMIT_VISIBILITY.DESCRIPTION",
              new Object[] {new lithium.service.translate.client.objects.Domain(domain.getName())}, "Loss Limit Visibility.",
              LocaleContextHolder.getLocale()))
          .message(messageSource.getMessage("SERVICE_USER_THRESHOLD.NOTIFICATIONS.LOSS_LIMIT_VISIBILITY.MESSAGE",
              new Object[] {new lithium.service.translate.client.objects.Domain(domain.getName())}, "Loss Limit Visibility.",
              LocaleContextHolder.getLocale()))
          .build());
    }
  }

  // LIMIT_TYPE_LOSS -> limit.type.loss
  private String typeNameInDotNotationLowerCase(String type) {
    return type.toLowerCase().replace("_", ".");
  }

  private String notificationName(Threshold threshold) {
    return threshold.getType().getName().toLowerCase().replaceAll("_", ".") + "." + threshold.getId();
  }

}
