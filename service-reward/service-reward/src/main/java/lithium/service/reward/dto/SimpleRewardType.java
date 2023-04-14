package lithium.service.reward.dto;

import lithium.service.reward.validation.constraints.ValidRewardNotificationMessage;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@Builder
@Accessors(fluent = true)
@ValidRewardNotificationMessage
public class SimpleRewardType {

    @NotBlank(message = "rewardTypeName is a required field")
    private String rewardTypeName;

    @NotBlank(message = "url is a required field")
    private String url;

    private String notificationMessage;

    private boolean instant;

    @Valid
    private List<SimpleRewardTypeValue> rewardTypeValues;

    @Valid
    private List<SimpleRewardTypeGame> rewardTypeGames;
}
