package lithium.service.access.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * Part of the audit trail for access rule executions. Stores core external provider response data.
 */
public class ExternalListTransactionData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("accessRuleTransaction")
	private AccessRuleTransaction accessRuleTransaction;

	@OneToOne
	@JoinColumn
	ExternalList externalList;

	// FIXME: Store basic output and outcome data
	// FIXME: Need to decide on a data type for the rule execution raw response data. Will live in another table
}
