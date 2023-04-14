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

/**
 * If a transaction_label_value is added asynchronously, that is, after the accounting balance adjustment has committed,
 * then we need to queue an entry to asynclabelvaluequeue.asynclabelvaluegroup. This is for period domain
 * summarisation in service-accounting-domain-summary. It ensures that the label value is summarised. Currently only
 * checking {@link lithium.service.accounting.provider.internal.services.TransactionService#summarizeAdditionalTransactionLabels(lithium.service.accounting.objects.TransactionLabelContainer)}.
 * This is the only place that adds transaction_label_value's asynchronously, some time after a transaction has
 * completed.
 */

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "transaction_label_value",
		indexes = {
				@Index(name = "idx_tlv_tranid", columnList = "transactionId", unique = false),
		}
)
public class TransactionLabelValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;

	@Column(nullable=false)
	private Long transactionId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(nullable=false)
	private LabelValue labelValue;
}
