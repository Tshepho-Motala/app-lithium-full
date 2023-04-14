package lithium.service.cashier.data.entities.backoffice;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ShortenCashierTransactionBO {
  private Date createdOn;
  private String transactionType;
  private String processor;
  private String descriptor;
  private BigDecimal amount;
  private String currencyCode;
  private String status;
}
