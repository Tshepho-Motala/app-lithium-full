package lithium.service.accounting.objects;

import java.io.Serializable;
import java.util.List;
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
public class CompleteSummaryAccountTransactionType implements Serializable {

  private String transactionType;
  private String createdOn;
  List<CompleteSummaryAccountTransactionTypeDetail> details;

}
