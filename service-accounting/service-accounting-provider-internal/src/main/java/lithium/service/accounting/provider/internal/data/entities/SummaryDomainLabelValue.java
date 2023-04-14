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

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "summary_domain_label_value",
		indexes = {
				@Index(name = "idx_pd_all", columnList = "period_id,transaction_type_id,account_code_id,label_value_id,currency_id", unique = true),
		}
)
public class SummaryDomainLabelValue {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private Long tranCount;
	@Column(nullable=false)
	private Long debitCents;
	@Column(nullable=false)
	private Long creditCents;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private LabelValue labelValue;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private TransactionType transactionType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private AccountCode accountCode;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Currency currency;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;

	private int tag;

}
