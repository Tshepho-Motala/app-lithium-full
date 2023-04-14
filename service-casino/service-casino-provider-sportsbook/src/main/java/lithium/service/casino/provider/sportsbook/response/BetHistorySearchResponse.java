package lithium.service.casino.provider.sportsbook.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lithium.service.casino.provider.sportsbook.storage.objects.Bet;
import lombok.Data;

/**
 *
 */
@Data
public class BetHistorySearchResponse {

  @JsonProperty("data") private List<Bet> bets;

  /**
   *
   */
  public BetHistorySearchResponse() {
  }
}
