package lithium.service.casino.provider.incentive.api.schema.placement.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementRequestEvent {
    private String sportName;
    private String sportCode;
    private String competitionCode;
    private String eventName;
    private long eventStartTime;
    private String marketCode;
    private String marketName;
    private double price;
    private String selectionCode;
    private String selectionId;
    private String selectionName;
}
