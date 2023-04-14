package lithium.service.casino.provider.incentive.context;

import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.settlement.PickAnySettlementResponse;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lithium.service.casino.provider.incentive.storage.entities.PickAnySettlement;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = { "entry", "settlement" })
@Builder
public class PickAnySettlementContext {

    PickAnySettlementRequest request;
    PickAnySettlementResponse response;
    String locale;
    PickAnySettlement settlement;
    PickAnyEntry entry;
}
