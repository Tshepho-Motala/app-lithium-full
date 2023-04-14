package lithium.service.user.provider.threshold.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lithium.services.user.threshold")
public class Properties {

  private Notifications notifications = new Notifications();
  private boolean calculateDayGranularityEnabled = false;
  private boolean calculateWeekGranularityEnabled = false;
  private boolean calculateMonthGranularityEnabled = false;
  private boolean calculateYearGranularityEnabled = false;
  private boolean calculateTotalGranularityEnabled = false;

  @Data
  public class Notifications {
    // When enabled, notifications will automatically be created on svc-notifications when a new threshold is created.
    private boolean enableAutoCreate = false;

    // When enabled, notifications will be sent when the user also has notifications enabled.
    private boolean sendNotifications = false;
  }

  public boolean sendNotifications() {
    if (notifications != null) {
      return notifications.isSendNotifications();
    }
    return false;
  }

  public boolean enableAutoCreate() {
    if (notifications != null) {
      return notifications.isEnableAutoCreate();
    }
    return false;
  }

}
