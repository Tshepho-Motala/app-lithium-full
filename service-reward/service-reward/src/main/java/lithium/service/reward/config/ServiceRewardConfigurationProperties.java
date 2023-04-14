package lithium.service.reward.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties( prefix = "lithium.service.reward" )
public class ServiceRewardConfigurationProperties {
  private int playerRewardHistoryPageSize;
  private int playerRewardCancelPageSize;
  private Jobs jobs = new Jobs();

  @Data
  public static class Jobs {
    private ExpiringRewardsCleanup expiringRewardsCleanup;
  }

  @Data
  public static class ExpiringRewardsCleanup {
    private String cron;
    private int pageSize = 1000;
  }
}