package lithium.service.user.threshold.data.entities;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerThresholdHistory implements Serializable {

  @Serial
  private static final long serialVersionUID = -2555117584750382128L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;
  @Version
  private int version;
  @Temporal( TemporalType.TIMESTAMP )
  private Date thresholdHitDate;
  private BigDecimal amount;
  private BigDecimal dailyLossLimit;
  private BigDecimal dailyLossLimitUsed;
  private BigDecimal weeklyLossLimit;
  private BigDecimal weeklyLossLimitUsed;
  private BigDecimal monthlyLossLimit;
  private BigDecimal monthlyLossLimitUsed;

  private BigDecimal depositAmount;
  private BigDecimal withdrawalAmount;
  private BigDecimal netLifetimeDepositAmount;
  @ManyToOne
  private User user;
  @ManyToOne
  private ThresholdRevision thresholdRevision;
  @Default
  private Boolean thresholdReachedMessage = Boolean.FALSE;
}
