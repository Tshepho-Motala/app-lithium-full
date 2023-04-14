package lithium.service.casino.provider.sportsbook.storage.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bet {

  private String betDate;
  private String betId;
  private String betName;
  private String betSettledDate;
  private String betStatus;
  private String brand;
  private String customerId;
  private Boolean isComboBonus;
  private String isFreeBet;
  private String purchaseId;
  @JsonProperty("return") private Double returnAmount;
  private String sbtCustomerId;
  private List<Selection> selections;
  private Double stake;
  private String totalOdds;
  private Integer betTypeId;

  /**
   *
   */
  public Bet() {

  }
}
