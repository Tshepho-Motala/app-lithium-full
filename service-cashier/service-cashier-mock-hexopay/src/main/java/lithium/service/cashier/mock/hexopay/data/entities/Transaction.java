package lithium.service.cashier.mock.hexopay.data.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import lithium.service.cashier.mock.hexopay.data.Scenario;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URI;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "hexopay_transaction",
    indexes = {
            @Index(name = "uid_idx", columnList = "uid", unique = true),
    }
)
public class Transaction {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  private String uid;
  @Column(name="tracking_id")
  private String trackingId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "transaction_token_id")
  private TransactionToken transactionToken;

  private Long amount;
  private String currency;

  @Column(name="created_at")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date createdAt;

  @Column(nullable=false)
  private String type;

  @Column(nullable=false)
  @Enumerated(EnumType.STRING)
  private Status status;

  private String message;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "card_id")
  private CreditCard card;

  @Column(name = "return_url")
  private String returnUrl;

  @Column(name = "notification_url")
  private String notificationUrl;

  @Column(name = "threed_secure")
  private boolean threeDSecure;

  @Column(nullable=false)
  @Enumerated(EnumType.ORDINAL)
  private Scenario scenario;

  @Column(nullable=false)
  private Long ttl;

  @Column(name="avs_reject", columnDefinition="char(1)")
  private String avsReject;
  @Column(name="cvc_reject", columnDefinition="char(1)")
  private String cvcReject;
  @Column(name="avs_cvc_status")
  private boolean avsCvcStatus;

  @PrePersist
  private void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = new Date();
    }
  }
}
