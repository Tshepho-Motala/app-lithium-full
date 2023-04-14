package lithium.service.casino.provider.incentive.api.schema.settlement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SettlementRequestSelection {
    private String selectionId;
    private String result;
}
