package lithium.service.accounting.domain.v2.storage.entities;

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

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	indexes = {
		@Index(name = "idx_summary_domain_label_value_shard", columnList = "period_id, transaction_type_id,"
				+ " account_code_id, label_value_id, currency_id, shard, testUsers", unique = true),
		@Index(name = "idx_summary_domain_label_value_all", columnList = "period_id, transaction_type_id,"
				+ " account_code_id, label_value_id, currency_id, testUsers", unique = false)
	}
)
public class SummaryDomainLabelValue {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	private int version;

	@Column(nullable = false)
	private String shard;

	@Column(nullable = false)
	@Builder.Default
	private Long tranCount = 0L;
	@Column(nullable = false)
	@Builder.Default
	private Long debitCents = 0L;
	@Column(nullable = false)
	@Builder.Default
	private Long creditCents = 0L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private LabelValue labelValue;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private TransactionType transactionType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private AccountCode accountCode;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Currency currency;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Period period;

	@Column(nullable = false)
	private boolean testUsers;
}
