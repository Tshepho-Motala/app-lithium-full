package lithium.service.casino.provider.sportsbook.request;

import lithium.service.casino.provider.sportsbook.data.MultipleSelection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BetHistorySearchRequest {

  private String betId;
  private List<MultipleSelection> betAmountTypeIn;
  private String[] betAmountType;
  private List<MultipleSelection> betTypeIn;
  private List<String> betType;
  private String brand;
  private String customerId;
  private String dateType;
  private List<String> eventsByLeague;
  private String from;
  private String matchType;
  private Integer page;
  private String sha256;
  private Integer size;
  private List<String> status;
  private List<String> sports;
  private List<String> marketTypesBySport;
  private List<String> leaguesBySport;
  private Long timestamp;
  private String to;
  private String sort;
  private String order;
}
