package lithium.service.casino.provider.roxor.api.schema.progressive;

import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProgressiveByWebsiteResponse {
    private SuccessStatus status;
    private List<ProgressiveByGameKey> progressiveAmountsByGameKeys;
}
