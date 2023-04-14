package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lithium.service.cashier.client.objects.enums.TransactionTagType;
import lithium.service.cashier.converter.EnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "cashier.TransactionTag")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode()
@Table(
    catalog = "lithium_cashier",
    name = "transaction_tag",
    indexes = {
        @Index(name = "idx_transaction_and_tag_type", columnList = "transaction_id, type_id", unique = true),
    }
)

public class TransactionTag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  @JsonBackReference
  private Transaction transaction;

  @Convert(converter = EnumConverter.TransactionTagTypeConverter.class)
  @Column(name = "type_id")
  private TransactionTagType type;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TransactionTag{");
    sb.append("id=").append(id);
    sb.append(", transactionId=").append(transaction.getId());
    sb.append(", type=").append(type);
    sb.append('}');
    return sb.toString();
  }
}
