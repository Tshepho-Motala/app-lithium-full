package lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetDebitReserveRequest {
    String guid;
    String betId;
    String purchaseId;
    Long reserveId;
    Long requestId;
    Double amount;
    Long timestamp;
    String sha256;
}
