package lithium.service.cashier.data.entities;

import static java.util.Optional.ofNullable;
import static lithium.service.cashier.client.objects.enums.TransactionTagType.WD_ON_BALANCE_LIMIT_RICHED;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "cashier.Transaction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"current", "linkedTransaction", "workflowHistoryList", "tags"})
@Table(
    catalog = "lithium_cashier",
    name = "transaction",
    indexes = {
        @Index(name = "idx_createdOn", columnList = "createdOn", unique = false),
        @Index(name = "idx_procRef", columnList = "processorReference", unique = false),
        @Index(name = "idx_additionalRef", columnList = "additionalReference", unique = false),
        @Index(name = "idx_tranType", columnList = "transactionType", unique = false),
        @Index(name = "idx_tranRetryProcessing", columnList = "retryProcessing", unique = false)
    }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Transaction implements Serializable {

  private static final long serialVersionUID = -969339311761532415L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonManagedReference
  private Long id;

  @Version
  private int version;

  @Column(nullable = false)
  private Date createdOn;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private DomainMethod domainMethod;

  @Column(nullable = true)
  private Boolean directWithdrawal;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true, name = "initiation_author")
  private User initiationAuthor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  @JsonManagedReference
  private TransactionWorkflowHistory current;

  @Column(nullable = true)
  private Long amountCents;

  @Column(nullable = true)
  private Long feeCents;

  @Column(nullable = false)
  private String currencyCode;

  private TransactionType transactionType;

  @Column(nullable = true)
  private String processorReference;

  private Long ttl;

  private String bonusCode;

  private String accountInfo;

  private boolean manual;

  private boolean forcedSuccess;

  private Long bonusId;

  private boolean retryProcessing; // When set to true the transaction will be processed by the scheduled retry job

  @JoinColumn(name = "linked_transaction_id", referencedColumnName = "id", nullable = true)
  @OneToOne(optional = true)
  @JsonBackReference
  private Transaction linkedTransaction; // Used to tieback a reversal to the original transaction and to include it as part of the workflow

  /**
   * Withdrawal. If the DMP is configured to reserve funds for withdrawal. Reference to accounting transaction for transfer from player_balance to
   * player_balance_pending_withdrawal.
   */
  @Column(nullable = true)
  private Long accRefToWithdrawalPending;

  /**
   * Withdrawal. In case of tran cancellation, declined, expired, reserved funds in player_balance_pending_withdrawal transferred back to
   * player_balance. Reference to accounting transaction for transfer from player_balance_pending_withdrawal back to player_balance.
   */
  @Column(nullable = true)
  private Long accRefFromWithdrawalPending;

  @Column(nullable = true)
  private String additionalReference;

  @Column(nullable = true)
  private Long sessionId;

  @JoinColumn(name = "payment_type_id", referencedColumnName = "id", nullable = true)
  @OneToOne(optional = true)
  private TransactionPaymentType transactionPaymentType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true, name = "reviewed_by")
  private User reviewedBy;

  @Column(length = 512, nullable = false)
  private String declineReason;

  @Column(nullable = true)
  private Integer errorCode;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "payment_method_id", referencedColumnName = "id", nullable = true)
  private ProcessorUserCard paymentMethod;

  @Transient
  private Boolean hasRemarks;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = true)
  private TransactionStatus status;

  @Builder.Default
  @OneToMany(fetch = FetchType.EAGER, mappedBy="transaction", cascade= CascadeType.ALL)
  private List<TransactionTag> tags = new ArrayList<>();

  @Column
  private Long manualCashierAdjustmentId;

  public boolean isAutoApproved() {
    return tags.stream()
        .anyMatch(tag -> TransactionTagType.AUTO_APPROVED.equals(tag.getType()));
  }

  public Long getRuntime() {
    return Optional.of(this.getCurrent())
        .map(current -> Math.round((current.getTimestamp().getTime() - getCreatedOn().getTime())/1000D))
        .orElse(0L);
  }

  @PrePersist
  private void prePersist() {
    if (this.createdOn == null) {
      this.createdOn = new Date();
    }
  }

  public void setDeclineReason(String declineReason) {
    if (declineReason != null) {
      try {
        int size = getClass().getDeclaredField("declineReason").getAnnotation(Column.class).length();
        if (size < declineReason.length()) {
          declineReason = declineReason.substring(0, size);
        }
      } catch (NoSuchFieldException ex) {
      } catch (SecurityException ex) {
      }
    }
    this.declineReason = declineReason;
  }

  public boolean hasTag(TransactionTagType tag){
    return this.getTags().stream()
        .anyMatch(transactionTag -> transactionTag.getType().equals(tag));
  }

  public boolean isWdOnBalanceLimitRiched(){
    return hasTag(WD_ON_BALANCE_LIMIT_RICHED);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Transaction.class.getSimpleName() + "(", ")")
        .add("id=" + id)
        .add("version=" + version)
        .add("createdOn=" + createdOn)
        .add("domainMethod=" + ofNullable(domainMethod)
            .map(dm -> "(" + dm.getId() + ", " + dm.getName() + ")")
            .orElse(null))
        .add("directWithdrawal=" + directWithdrawal)
        .add("initiationAuthor=" + ofNullable(initiationAuthor)
            .map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
            .orElse(null))
        .add("user=" + ofNullable(user)
            .map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
            .orElse(null))
        .add("current.id=" + ofNullable(current)
            .map(TransactionWorkflowHistory::getId)
            .orElse(null))
        .add("amountCents=" + amountCents)
        .add("feeCents=" + feeCents)
        .add("currencyCode='" + currencyCode + "'")
        .add("transactionType=" + transactionType)
        .add("processorReference='" + processorReference + "'")
        .add("ttl=" + ttl)
        .add("bonusCode='" + bonusCode + "'")
        .add("accountInfo='" + accountInfo + "'")
        .add("manual=" + manual)
        .add("forcedSuccess=" + forcedSuccess)
        .add("bonusId=" + bonusId)
        .add("retryProcessing=" + retryProcessing)
        .add("linkedTransaction.id=" + ofNullable(linkedTransaction)
            .map(Transaction::getId)
            .orElse(null))
        .add("accRefToWithdrawalPending=" + accRefToWithdrawalPending)
        .add("accRefFromWithdrawalPending=" + accRefFromWithdrawalPending)
        .add("additionalReference='" + additionalReference + "'")
        .add("sessionId=" + sessionId)
        .add("transactionPaymentType=" + transactionPaymentType)
        .add("reviewedBy=" + reviewedBy)
        .add("declineReason='" + declineReason + "'")
        .add("errorCode=" + errorCode)
        .add("paymentMethod.id=" + ofNullable(paymentMethod)
            .map(ProcessorUserCard::getId)
            .orElse(null))
        .add("hasRemarks=" + hasRemarks)
        .add("status=" + ofNullable(status)
            .map(TransactionStatus::getCode)
            .orElse(null))
        .add("tags=" + ofNullable(tags)
            .map(tag -> tag.stream()
                .map(TransactionTag::getType)
                .map(TransactionTagType::getName)
                .collect(Collectors.joining(",", "[", "]")))
            .orElse(null))
        .add("manualCashierAdjustmentId=" + manualCashierAdjustmentId)
        .toString();
  }
}
