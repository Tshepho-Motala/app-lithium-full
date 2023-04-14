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
		name = "summary_account",
		indexes = {
				@Index(name = "idx_pd_all", columnList = "period_id,account_id", unique = true),
//				@Index(name = "idx_pd_damaged", columnList = "damaged", unique = false),
		}
)
public class SummaryAccount {
	
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
	@Column(nullable=false)
	private Long openingBalanceCents;
	@Column(nullable=false)
	private Long closingBalanceCents;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Period period;
	
	@Column(nullable=false)
	private boolean damaged;
	
}
