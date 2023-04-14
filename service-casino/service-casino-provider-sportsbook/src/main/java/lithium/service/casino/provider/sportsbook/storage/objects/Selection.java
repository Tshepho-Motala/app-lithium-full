package lithium.service.casino.provider.sportsbook.storage.objects;

import lombok.Data;

/**
 *
 */
@Data
public class Selection {

  private String eventDate;
  private Integer eventId;
  private String eventName;
  private String id;
  private Integer leagueId;
  private String leagueName;
  private String marketName;
  private String name;
  private String odds;
  private String settlementScore;
  private Integer sportId;
  private String sportName;
  private String status;

  /**
   *
   */
  public Selection() {
  }
}
