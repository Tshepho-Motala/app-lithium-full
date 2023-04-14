package lithium.service.accounting.objects;

import lithium.service.client.objects.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class CompleteSummaryAccountTransactionTypeDetail {

  private SummaryAccountTransactionType summaryAccountTransactionType;
  private Integer granularity;
  private Long netLossToHouse;
}