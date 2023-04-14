package lithium.service.casino.search.objects;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetSummaryFE {
  private Long id;
  private String betRoundGuid;
  private Date date;
  private Double stake;
  private Double won;
  private Double loss;
  private String transactionType;
  private Long amountCents;
  private String transactionTypeDisplay;
  private String provider;
  private String providerTranId;
  private String gameName;
  private String gameProvider;
  private String gameCategory;
}
