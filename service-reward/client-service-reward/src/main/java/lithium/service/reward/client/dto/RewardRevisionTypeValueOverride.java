package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RewardRevisionTypeValueOverride implements Serializable {

  private static final long serialVersionUID = -3671901784725455403L;
  private String value;

  private Long rewardTypeFieldId;

  private Long rewardRevisionTypeId;
}
