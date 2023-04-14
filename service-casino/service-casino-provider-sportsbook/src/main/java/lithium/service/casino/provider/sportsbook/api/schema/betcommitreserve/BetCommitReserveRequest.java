package lithium.service.casino.provider.sportsbook.api.schema.betcommitreserve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetCommitReserveRequest {
    String guid;
    Long reserveId;
    Long timestamp;
    String sha256;
}
