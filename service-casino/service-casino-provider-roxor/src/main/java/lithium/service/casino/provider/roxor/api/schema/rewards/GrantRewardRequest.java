package lithium.service.casino.provider.roxor.api.schema.rewards;

import lithium.service.casino.provider.roxor.api.schema.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GrantRewardRequest {
    private Reward reward;
}
