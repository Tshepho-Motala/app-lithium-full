package lithium.service.casino.provider.sportsbook.api.schema.betinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetInfoResponseBet {
    String betId;
    String url;
}
