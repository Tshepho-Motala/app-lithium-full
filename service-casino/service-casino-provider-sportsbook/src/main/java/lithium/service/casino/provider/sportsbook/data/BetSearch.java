package lithium.service.casino.provider.sportsbook.data;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class BetSearch {
  private String betDate;
  private String betId;
  private String betName;
  private String betSettledDate;
  private String[] betStatus;
  private String brand;
  private String customerId;
  private Boolean isComboBonus;
  private Boolean isFreeBet;
  private String purchaseId;
  @JsonProperty("return") private Double returnAmount;
  private String sbtCustomerId;
  private List<BetSelection> selections;
  private Double stake;
  private String totalOdds;
}
