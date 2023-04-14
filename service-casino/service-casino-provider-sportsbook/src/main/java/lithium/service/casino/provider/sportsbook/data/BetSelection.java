package lithium.service.casino.provider.sportsbook.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BetSelection {
  private String eventDate;
  private Integer eventId;
  private String eventName;
  private String id;
  private Integer leagueId;
  private String leagueName;
  private String marketId;
  private String marketName;
  private String name;
  private String odds;
  private String settlementScore;
  private Integer sportId;
  private String sportName;
  private String[] status;
}
