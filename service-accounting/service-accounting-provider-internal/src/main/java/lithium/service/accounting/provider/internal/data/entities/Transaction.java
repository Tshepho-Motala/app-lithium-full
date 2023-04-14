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
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "transaction",
		indexes = {
				@Index(name = "idx_trx_created", columnList = "createdOn"),
//				@Index(name = "idx_trx_closed", columnList = "closedOn"),
//				@Index(name = "idx_trx_open", columnList = "open"),
//				@Index(name = "idx_trx_cancelled", columnList = "cancelled")
		}
)
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private Boolean open = true;

	@Column(nullable=false)
	private Boolean cancelled = false;

	@Column(nullable=false)
	private Date createdOn;
	
	@Column(nullable=true)
	private Date closedOn;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User author;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private TransactionType transactionType;

}
