package lithium.service.access.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.service.access.jobs")
@Configuration
public class Properties {
  private RemoveRawDataRecords removeRawDataRecords = new RemoveRawDataRecords();

  @Data
  public static class RemoveRawDataRecords {
    private String cron;
    private int days;
    private int pageSize = 1000;
  }
}
