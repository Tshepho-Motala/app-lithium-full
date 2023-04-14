package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity(name = "accounting.provider.internal.BalanceLimit")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "balance_limit",
	indexes = {
		@Index(name = "idx_bl_balance", columnList = "balanceCents", unique = false),
		@Index(name = "idx_bl_all", columnList = "account_id, contra_account_id, transaction_type_to_id", unique = true)
	}
)
public class BalanceLimit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;

	@Column(nullable = false)
	private Long balanceCents;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	Account contraAccount;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private TransactionType transactionTypeTo;
}
