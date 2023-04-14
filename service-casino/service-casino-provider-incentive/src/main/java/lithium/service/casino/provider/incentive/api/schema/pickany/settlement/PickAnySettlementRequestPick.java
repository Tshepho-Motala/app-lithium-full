package lithium.service.casino.provider.incentive.api.schema.pickany.settlement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PickAnySettlementRequestPick {
    Long eventId;
    Long eventHomeScore;
    Long eventAwayScore;
    Long pointsResult;
}
