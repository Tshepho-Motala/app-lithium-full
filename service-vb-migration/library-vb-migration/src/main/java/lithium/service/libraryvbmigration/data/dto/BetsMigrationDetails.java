package lithium.service.libraryvbmigration.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetsMigrationDetails {
  private String playerGuid;
  private String domainName;
  private String customerId;
  private String currencyCode;
  private double amount;
  private double returns;
  private String betId; //will be used to populate betTransactionId betResultTransactionId and roundId
  private String providerGuid;
  private String providerGameId;
  private long placementDateTime; //from placementDateTime
  private long settlementDateTime; //from placementDateTime
}
