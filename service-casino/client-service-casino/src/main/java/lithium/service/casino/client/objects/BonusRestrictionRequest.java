package lithium.service.casino.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BonusRestrictionRequest {
    private long playerId;
    private String playerGuid;
    private boolean restricted;
    private Integer retryCount;
    private Integer retryTotal;
}

