package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString(exclude = "transaction")
@EqualsAndHashCode(exclude = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "transaction_remark",
    indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp", unique = false)
    })
public class TransactionRemark {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Date timestamp;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonIgnore
  private Transaction transaction;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User author;

  @Column(nullable = false, length = 2000)
  private String message;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "remark_type_id")
  @JsonIgnore
  private TransactionRemarkType type;

  @PrePersist
  private void prePersist() {
    if (timestamp == null) {
      timestamp = new Date();
    }
  }
}
