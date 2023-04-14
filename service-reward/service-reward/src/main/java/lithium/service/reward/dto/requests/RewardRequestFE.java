package lithium.service.reward.dto.requests;

import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RewardRequestFE {
    private Integer page;
    private Integer pageSize;
    public boolean active;
    private String status;
}
