package lithium.service.casino.provider.sportsbook.data;

import lithium.service.casino.CasinoTranType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubBetPlacementMessage {
    private CasinoTranType eventType;
    private String accountGuid;
    private String accountId;
    private Double value;
    private String product;
    private Long reserveId;
    private Long timestamp;
    private String betId;
}
