package lithium.service.cashier.data.entities.frontend;

import java.util.Date;
import java.util.List;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionComment;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TransactionWorkflowHistoryFE {
  private Long id;
  private User author;
  private String authorName;
  private Date timestamp;
  private Transaction transaction;
  private DomainMethodProcessor processor;
  private TransactionStatus status;
  private User assignedTo;
  private Long accountingReference;
  private Integer stage;
  private String source;
  private String billingDescriptor;
  private List<TransactionComment> comments;
}
