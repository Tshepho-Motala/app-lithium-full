package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
		name = "account_label_value_constraint",
		indexes = {
				@Index(name = "idx_alvc_all", columnList = "account_id, label_value_id", unique = true)
		}
)
public class AccountLabelValueConstraint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Account account;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private LabelValue labelValue;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private TransactionEntry transactionEntry;
}
