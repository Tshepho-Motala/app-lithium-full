package lithium.service.reward.provider.client.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lithium.service.reward.client.dto.FieldDataType;
import lithium.service.reward.client.dto.RewardRevisionTypeValue;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.client.dto.RewardTypeField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
public class ProcessRewardTypeValue implements Serializable {

  private long rewardRevisionTypeValueId;
  private String rewardRevisionTypeValue;
  private long rewardTypeFieldId;
  private String rewardTypeFieldName;
  private FieldDataType rewardTypeFieldDataType;
  private long rewardRevisionTypeId;
  private boolean rewardRevisionTypeInstant;
}
