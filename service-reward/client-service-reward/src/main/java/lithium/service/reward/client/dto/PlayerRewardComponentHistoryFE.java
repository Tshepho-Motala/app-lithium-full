package lithium.service.reward.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRewardComponentHistoryFE {

    private Long id;
    private String status; //PlayerRewardComponentStatus
    private String rewardComponentName;
    private Long playerRewardHistoryId;
    private String playerGuid;
    private String awardedOn;
    private String created;
    private String updated;
    private double amountGiven;
    private double amountUsed;
    private double amountInCents;
    private String description;

    @Builder.Default
    private List<RewardGameFE> gameList = new ArrayList<>();
}
