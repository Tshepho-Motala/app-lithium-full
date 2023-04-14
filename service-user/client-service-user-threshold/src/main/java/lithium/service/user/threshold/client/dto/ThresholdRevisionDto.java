package lithium.service.user.threshold.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class ThresholdRevisionDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -4773364566521955386L;
  private long id;
  private BigDecimal percentage;
  private BigDecimal amount;
  private Date createdDate;
  private UserDto createdBy;

//  @JsonManagedReference( "threshold" )
  //  @JsonBackReference( "thresholdRevision" )
  private ThresholdDto threshold;

  public String toString() {
    return new StringJoiner(", ", ThresholdRevisionDto.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("percentage=" + percentage)
        .add("amount=" + amount)
        .add("createdDate=" + createdDate)
        .add("createdBy=" + createdBy)
        .add("threshold=" + threshold.getId())
        .toString();
  }
}
