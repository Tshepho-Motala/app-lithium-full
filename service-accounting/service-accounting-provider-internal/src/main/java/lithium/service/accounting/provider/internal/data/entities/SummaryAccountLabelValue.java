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

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "summary_account_label_value",
		indexes = {
				@Index(name = "idx_salv_all", columnList = "period_id,transaction_type_id,account_id,label_value_id", unique = true),
//				@Index(name = "idx_salv_damaged", columnList = "damaged", unique = false),
				@Index(name = "idx_label_value_tran_type", columnList = "label_value_id, transaction_type_id")
		}
)
public class SummaryAccountLabelValue implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private Account account;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;

	@Column(nullable=false)
	private boolean damaged;
}
