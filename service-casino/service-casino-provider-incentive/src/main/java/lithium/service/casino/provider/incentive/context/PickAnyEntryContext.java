package lithium.service.casino.provider.incentive.context;

import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryRequest;
import lithium.service.casino.provider.incentive.api.schema.pickany.entry.PickAnyEntryResponse;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = { "entry" })
public class PickAnyEntryContext {

    PickAnyEntryRequest request;
    PickAnyEntryResponse response;
    PickAnyEntry entry;
    String domainName;
    String playerGuid;
    String locale;
    Long sessionId;

}
