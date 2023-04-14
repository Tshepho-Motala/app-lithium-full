package lithium.service.casino.provider.sportsbook.api.schema.betcancelreserve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetCancelReserveRequest {
    String guid;
    Long reserveId;
    Long timestamp;
    String sha256;
}
