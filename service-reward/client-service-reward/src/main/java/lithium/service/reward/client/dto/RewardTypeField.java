package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RewardTypeField implements Serializable {

  private static final long serialVersionUID = 762178420526622247L;
  private Long id;
  @JsonBackReference("rewardType")
  private RewardType rewardType;
  private String name; // e.g. freespins
  private FieldDataType dataType; // e.g. number
  private String description; // e.g. Please specify the amount of freespins to award.
}