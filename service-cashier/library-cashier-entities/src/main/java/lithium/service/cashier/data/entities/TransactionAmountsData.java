package lithium.service.cashier.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAmountsData {

  private Long depositAmountCents;
  private Long feeAmountCents;
}
