package lithium.service.casino.provider.incentive.api.schema.pickany.entry;

import lombok.Data;
import lombok.ToString;

@Data
@ToString

public class PickAnyEntryRequestPick {
    Long eventId;
    String eventName;
    Long eventStartTime;
    Long homeScore;
    Long awayScore;
}
