package lithium.service.casino.provider.sportsbook.api.schema.betinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetInfoRequest {
    List<BetInfoRequestBet> bets;
    Long timestamp;
    String sha256;
}
