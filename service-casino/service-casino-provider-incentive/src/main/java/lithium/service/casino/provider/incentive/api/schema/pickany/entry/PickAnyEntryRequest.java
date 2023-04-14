package lithium.service.casino.provider.incentive.api.schema.pickany.entry;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PickAnyEntryRequest {
    String gameCode;
    Long predictorId;
    String competitionCode;
    String entryTransactionId;
    List<PickAnyEntryRequestPick> picks;
    Long entryTimestamp;
    String sha256;
    String extraData;
}
