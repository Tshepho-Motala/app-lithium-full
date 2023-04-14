package lithium.service.cashier.mock.smartcash.data.entities;

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

import lithium.service.cashier.mock.smartcash.data.Scenario;
import lithium.service.cashier.processor.smartcash.data.enums.SmartcashTransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "smartcash_transaction",
    indexes = {
            @Index(name = "reference_idx", columnList = "reference", unique = true),
    }
)
public class Transaction {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(name="reference")
  private String reference;

  private String amount;
  private String currency;
  private String country;

  @Column(name="created_at")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date createdAt;

  @Column(nullable=false)
  private String type;

  @Column(nullable=false)
  @Enumerated(EnumType.STRING)
  private SmartcashTransactionStatus status;

  private String message;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Column(nullable=false)
  @Enumerated(EnumType.ORDINAL)
  private Scenario scenario;

  @PrePersist
  private void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = new Date();
    }
  }
}
