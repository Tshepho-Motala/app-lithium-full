package lithium.service.reward.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RewardRevisionTypeValue implements Serializable {

  private static final long serialVersionUID = 4945600401754142706L;
  private long id;

  private String value;

  private RewardTypeField rewardTypeField;

  private RewardRevisionType rewardRevisionType;
}
