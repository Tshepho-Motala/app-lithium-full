package lithium.service.user.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "user.TransactionTypeAccount")
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "lithium_user",
    name = "transaction_type_account", indexes = {
    @Index(name = "idx_tta_all", columnList = "accountTypeCode") //, transaction_type_id")
})
public class TransactionTypeAccount {

  @Version
  int version;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(nullable = false)
  private String accountTypeCode;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(nullable=false)
//	private TransactionType transactionType;

  @Column(nullable = false)
  private boolean debit;

  @Column(nullable = false)
  private boolean credit;
}
