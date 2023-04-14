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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "threshold_revision")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdRevision implements Serializable {

  @Serial
  private static final long serialVersionUID = 5209484378055791454L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Version
  private int version;
  private BigDecimal percentage;
  private BigDecimal amount;
  private int granularity;
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;
  @OneToOne
  private User modifiedBy;
  @OneToOne
  private User createdBy;
  @ManyToOne
  private Domain domain;
  @ManyToOne
  private Type type;
}
