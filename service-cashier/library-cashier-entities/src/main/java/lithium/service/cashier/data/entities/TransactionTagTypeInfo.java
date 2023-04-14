package lithium.service.cashier.data.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@Entity(name = "cashier.TransactionTagType")
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "tag_type",
    indexes = {
        @Index(name = "idx_name", columnList = "name", unique = true)
    })
// This table used by clients which work with DB data only (mostly DWH team). Because they need to have mapping between tag_type_id and tag_type_name.
// For developers easier to work with transaction_tag_type as enum. Therefore, there is no relation in Java classes between
// TransactionTagTypeInfo and TransactionTag. But TransactionTagTypeInfo populating with new values if it added
public class TransactionTagTypeInfo implements Serializable {

  @Id
  private Integer id;

  @Column(nullable = false)
  private String name;

}
