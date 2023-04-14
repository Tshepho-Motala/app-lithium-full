package lithium.service.accounting.provider.internal.data.entities;

import lithium.service.accounting.objects.TransactionEntryBODetails;
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
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "transaction_entry",
		indexes = {
				@Index(name = "idx_te_tranid", columnList = "transaction_id", unique = false),
				@Index(name = "idx_tx_date", columnList = "date", unique = false),
//				@Index(name = "idx_tx_amount", columnList = "amountCents", unique = false)
		}
)
public class TransactionEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = true)
	private Transaction transaction;

	@Column(nullable=false)
	private Date date;
	
	@Column(nullable=false)
	private Long amountCents;
	
	@Column(nullable=true)
	private Long postEntryAccountBalanceCents;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Account account;

	@Transient
	private TransactionEntryBODetails details;

}
