package lithium.service.casino.provider.sportsbook.data;

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
public class ExternalBonusRestrictionRequest {
    private long playerId;
    private boolean restrict;
    private String sha;
}
