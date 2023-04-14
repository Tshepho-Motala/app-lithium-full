package lithium.service.user.provider.threshold.data.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThresholdHistoryBO {
  private String guid;
  private String thresholdHitDate;
  private String playerName;
  private String dailyLimit;
  private String dailyLimitUsed;
  private String weeklyLimit;
  private String weeklyLimitUsed;
  private String monthlyLimit;
  private String monthlyLimitUsed;
  private String thresholdHit;
  private String accountId;
  private String weeklyThreshold;
  private String monthlyThreshold;
  private String dailyThreshold;

}
