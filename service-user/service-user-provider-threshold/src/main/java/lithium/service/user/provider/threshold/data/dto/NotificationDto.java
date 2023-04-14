package lithium.service.user.provider.threshold.data.dto;

import lombok.Data;

@Data
public class NotificationDto {
  private boolean activate;
  private String domainName;
  private String userGuid;
}
