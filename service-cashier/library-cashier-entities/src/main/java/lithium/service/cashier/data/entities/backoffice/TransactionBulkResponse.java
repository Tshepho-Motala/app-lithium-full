package lithium.service.cashier.data.entities.backoffice;

import java.util.Date;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TransactionBulkResponse {
  private Long id;
  private Long amountCents;
  private Date createdOn;
  private boolean autoApproved;
  private String declineReason;
  private Boolean directWithdrawal;
  private Long feeCents;
  private boolean manual;
  private TransactionType transactionType;
  private boolean testAccount;
  private String currencyCode;
  private Long sessionId;
  private User user;
  private TransactionStatus status;
  private String processorDescription;
  private ProcessorUserCard paymentMethod;
  private boolean canApprove;
  private boolean canOnHold;
  private String comment;
}
