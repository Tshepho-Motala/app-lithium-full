package lithium.service.user.threshold.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringJoiner;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThresholdRevision implements Serializable {

  @Serial
  private static final long serialVersionUID = -4665315098238955000L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;
  @Version
  private int version;
  private BigDecimal percentage;
  private BigDecimal amount;
  @Temporal( TemporalType.TIMESTAMP )
  private Date createdDate;
  @OneToOne
  private User createdBy;

  @JsonBackReference( "threshold" )
  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private Threshold threshold;

  public String toShortString() {
    return new StringJoiner(", ", ThresholdRevision.class.getSimpleName() + "[", "]").add("id=" + id)
        .add("percentage=" + percentage)
        .add("amount=" + amount)
        .add("createdDate=" + createdDate)
        .add("createdBy=" + createdBy.toShortString())
        .add("threshold=" + threshold.getId())
        .toString();
  }
}