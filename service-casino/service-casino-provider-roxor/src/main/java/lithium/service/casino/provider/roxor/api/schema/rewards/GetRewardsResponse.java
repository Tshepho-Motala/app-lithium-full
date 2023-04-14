package lithium.service.casino.provider.roxor.api.schema.rewards;

import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.Reward;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class GetRewardsResponse {
    private SuccessStatus status;
    private List<Reward> rewards;
}
