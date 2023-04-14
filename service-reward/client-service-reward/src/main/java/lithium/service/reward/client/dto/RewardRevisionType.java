package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@ToString( exclude = {"rewardRevision"} )
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RewardRevisionType implements Serializable {

  private static final long serialVersionUID = 1597027816322054969L;

  private long id;
  private RewardType rewardType;

  @JsonBackReference( "rewardRevision" )
  private RewardRevision rewardRevision;

  /**
   * if this is false, then player first needs to acknowledge the reward type. if this is true, the call is made to external provider to award the
   * reward.
   */
  private boolean instant;

  public String rewardTypeUrl() {
    return rewardType.getUrl();
  }
}
