package lithium.service.access.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Storage class for a configured output to outcome map for a specific rule template.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
@ToString(exclude = "accesscontrollist")
public class AccessControlListRuleStatusOptionConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	int version;

	@JoinColumn
	@ManyToOne
	@JsonBackReference
	private AccessControlList accesscontrollist;

	@JoinColumn
	@ManyToOne
	private AccessRuleResultStatusOptions output;

	@JoinColumn
	@ManyToOne
	private AccessRuleResultStatusOptions outcome;
}
