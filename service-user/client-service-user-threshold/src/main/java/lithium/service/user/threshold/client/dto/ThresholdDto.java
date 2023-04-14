package lithium.service.user.threshold.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import lithium.service.client.objects.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class ThresholdDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -6395543871135567263L;
  private long id;
//  @JsonBackReference( "threshold" )
  //  @JsonManagedReference( "thresholdRevision" )
  private ThresholdRevisionDto current;

  private boolean active = true;
  private Integer ageMin;
  private Integer ageMax;

  private DomainDto domain;
  private TypeDto type;

  @JsonFormat( shape = JsonFormat.Shape.STRING )
  private Granularity granularity;

  @Override
  public String toString() {
    return new StringJoiner(", ", ThresholdDto.class.getSimpleName() + "[", "]").add("id=" + id)
        .add("current=" + current)
        .add("active=" + active)
        .add("ageMin=" + ageMin)
        .add("ageMax=" + ageMax)
        .add("domain=" + domain)
        .add("type=" + type)
        .add("granularity=" + granularity)
        .toString();
  }
}
