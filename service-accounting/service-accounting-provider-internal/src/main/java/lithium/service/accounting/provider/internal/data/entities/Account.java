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

@Entity(name = "accounting.provider.internal.Account")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "account",
		indexes = {
//				@Index(name = "idx_acc_balance", columnList = "balanceCents", unique = false),
				@Index(name = "idx_acc_all", columnList = "currency_id, owner_id, domain_id, account_type_id, account_code_id", unique = true)
		}
)
public class Account implements Serializable {

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
	private Currency currency;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private User owner;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Domain domain;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	AccountType accountType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	AccountCode accountCode;
}
