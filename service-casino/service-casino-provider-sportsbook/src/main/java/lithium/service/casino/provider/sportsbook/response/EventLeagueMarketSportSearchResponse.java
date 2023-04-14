package lithium.service.casino.provider.sportsbook.response;

import lithium.service.casino.provider.sportsbook.storage.objects.LeagueMarketSport;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class EventLeagueMarketSportSearchResponse {

  private List<LeagueMarketSport> data;

  /**
   *
   */
  public EventLeagueMarketSportSearchResponse() {
  }
}
