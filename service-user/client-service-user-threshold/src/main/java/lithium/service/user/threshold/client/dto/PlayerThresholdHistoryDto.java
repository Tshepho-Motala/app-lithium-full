package lithium.service.user.threshold.client.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerThresholdHistoryDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 6703258665486210595L;
  private long id;
  private Date thresholdHitDate;
  private BigDecimal amount;
  private BigDecimal dailyLimit;
  private BigDecimal dailyLimitUsed;
  private BigDecimal weeklyLimit;
  private BigDecimal weeklyLimitUsed;
  private BigDecimal monthlyLimit;
  private BigDecimal monthlyLimitUsed;
  private String defaultDomainCurrencySymbol;
  private UserDto user;
  private ThresholdRevisionDto thresholdRevision;
  private String triggerType;
  private BigDecimal depositAmount;
  private BigDecimal withdrawalAmount;
  private BigDecimal netLifetimeDepositAmount;
  private Date accountCreationDate;
  //TODO: this is a hack for granularity not wanting to serialize on feign call. I don't have time to figure out why.
  private String granularity;
}
