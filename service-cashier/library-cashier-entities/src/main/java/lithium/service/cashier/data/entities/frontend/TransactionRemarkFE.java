package lithium.service.cashier.data.entities.frontend;

import java.util.Date;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TransactionRemarkFE {
  private Long id;
  private Date timestamp;
  private Transaction transaction;
  private User author;
  private String authorName;
  private String message;
}
