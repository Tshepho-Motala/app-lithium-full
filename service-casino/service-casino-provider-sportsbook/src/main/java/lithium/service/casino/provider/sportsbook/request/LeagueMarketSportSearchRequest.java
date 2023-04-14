package lithium.service.casino.provider.sportsbook.request;

import java.util.List;
import lithium.service.casino.provider.sportsbook.data.MultipleSelection;
import lombok.Data;

/**
 *
 */
@Data
public class LeagueMarketSportSearchRequest {
  private String brand;
  private List<String> sports;
  private String sha256;
  private Long timestamp;

  /**
   *
   */
  public LeagueMarketSportSearchRequest() {
  }
}
