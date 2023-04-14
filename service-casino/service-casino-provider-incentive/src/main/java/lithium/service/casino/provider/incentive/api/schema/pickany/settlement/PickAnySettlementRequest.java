package lithium.service.casino.provider.incentive.api.schema.pickany.settlement;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PickAnySettlementRequest {
    String entryTransactionId;
    String settlementTransactionId;
    Long settlementTimestamp;
    List<PickAnySettlementRequestPick> picks;
    Long totalPointsResult;
    String sha256;
    String extraData;
}
