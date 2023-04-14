package lithium.service.casino.provider.roxor.api.schema.progressive;

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
public class Progressive {
    private String progressiveId;
    private List<ProgressiveAmount> progressiveAmounts;
}
