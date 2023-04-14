package lithium.service.user.threshold.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan
@ConfigurationProperties( prefix = "lithium.service.user.threshold" )
public class ThresholdProperties {

  private Notifications notifications = new Notifications();
  private boolean calculateDayGranularityEnabled = false;
  private boolean calculateWeekGranularityEnabled = false;
  private boolean calculateMonthGranularityEnabled = false;
  private boolean calculateYearGranularityEnabled = false;
  private boolean calculateTotalGranularityEnabled = false;

  @Data
  public class Notifications {

    // When enabled, notifications will be sent when the user also has notifications enabled.
    private boolean sendNotifications = false;
  }

  public boolean sendNotifications() {
    if (notifications != null) {
      return notifications.isSendNotifications();
    }
    return false;
  }


}
