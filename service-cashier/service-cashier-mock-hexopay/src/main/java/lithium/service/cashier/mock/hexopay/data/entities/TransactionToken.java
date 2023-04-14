package lithium.service.cashier.mock.hexopay.data.entities;

import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
import javax.persistence.Version;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "hexopay_transaction_token",
    indexes = {
            @Index(name = "token_idx", columnList = "token", unique = true),
    }
)
public class TransactionToken {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(name="tracking_id")
  private String trackingId;
  private String token;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "transaction_id")
  private Transaction transaction;

  @Column(name = "return_url")
  private String returnUrl;

  @Column(name = "notification_url")
  private String notificationUrl;

  @Column(nullable=false)
  private Long ttl;

  @Column(name="avs_reject_codes")
  private String avsRejectCodes;
  @Column(name="cvc_reject_codes")
  private String cvcRejectCodes;

  @PrePersist
  private void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = new Date();
    }
  }
}
