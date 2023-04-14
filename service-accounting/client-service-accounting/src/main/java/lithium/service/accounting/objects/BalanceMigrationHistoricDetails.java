package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMigrationHistoricDetails {
  private String userGuid;
  private String domainName;
  private String customerId;
  private long openingBalancePhase1;
  private long openingBalancePhase2;
}
