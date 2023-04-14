package lithium.service.casino.provider.roxor.api.schema.rewards;

import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorStatus;
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
public class GrantRewardResponse {
    private SuccessStatus status;
    private ErrorStatus errorStatus;
}
