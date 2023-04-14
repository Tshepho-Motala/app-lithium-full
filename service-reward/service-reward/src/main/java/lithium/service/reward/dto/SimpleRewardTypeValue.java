package lithium.service.reward.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Accessors(fluent = true)
public class SimpleRewardTypeValue {
    @NotBlank(message = "rewardTypeFieldName is a required field")
    private String rewardTypeFieldName;
    @NotBlank(message = "value is a required field")
    private String value;
}