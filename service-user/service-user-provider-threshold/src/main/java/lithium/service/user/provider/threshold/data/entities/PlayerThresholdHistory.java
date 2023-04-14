package lithium.service.user.provider.threshold.data.entities;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "player_threshold_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerThresholdHistory implements Serializable {

  @Serial
  private static final long serialVersionUID = -2555117584750382128L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Version
  private int version;
  @Temporal(TemporalType.TIMESTAMP)
  private Date thresholdHitDate;
  private BigDecimal amount;
  private BigDecimal dailyLimit;
  private BigDecimal dailyLimitUsed;
  private BigDecimal weeklyLimit;
  private BigDecimal weeklyLimitUsed;
  private BigDecimal monthlyLimit;
  private BigDecimal monthlyLimitUsed;
  @ManyToOne
  private User user;
  @ManyToOne
  private ThresholdRevision thresholdRevision;


}
