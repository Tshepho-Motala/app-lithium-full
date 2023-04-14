package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
	name = "transaction_type_label",
	indexes = {
		@Index(name = "idx_ttl_all", columnList = "label, transaction_type_id")
	}
)
public class TransactionTypeLabel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;

	@Column(nullable = false)
	private String label;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private TransactionType transactionType;
	
	@Column(nullable = false)
	private boolean summarize;

	@Column(nullable = true)
	private Boolean summarizeTotal;

	@Column(nullable = true)
	private Boolean synchronous;
	
	private String accountTypeCode;

	private boolean optional;
}
