package lithium.service.cashier.data.entities.backoffice;

import java.util.Date;
import java.util.List;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class CashierTransactionBO {

  private Long id;
  private int version;
  private Date createdOn;
  private Date updatedOn;
  private DomainMethod domainMethod;
  private Boolean directWithdrawal;
  private String initiationAuthorFullName;
  private User user;
  private TransactionWorkflowHistory current;
  private Long amountCents;
  private Long feeCents;
  private String currencyCode;
  private TransactionType transactionType;
  private String processorReference;
  private Long ttl;
  private String bonusCode;
  private String accountInfo;
  private boolean manual;
  private boolean forcedSuccess;
  private Long bonusId;
  private boolean retryProcessing;
  private Transaction linkedTransaction;
  private Long accRefToWithdrawalPending;
  private Long accRefFromWithdrawalPending;
  private String additionalReference;
  private Long sessionId;
  private TransactionPaymentType transactionPaymentType;
  private boolean testAccount;
  private boolean autoApproved;
  private String reviewedByFullName;
  private String declineReason;
  private ProcessorUserCard paymentMethod;
  private Boolean hasRemarks;
  private Long runtime;
  private Long reviewedById;
  private List<String> tags;
  private List<ManualCashierAdjustmentAccountCode> manualCashierAdjustmentAccountCodes;
  private Long manualCashierAdjustmentId;
}
