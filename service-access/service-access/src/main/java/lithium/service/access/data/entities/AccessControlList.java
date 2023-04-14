package lithium.service.access.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lithium.service.access.client.objects.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_access_control_list_id", columnList="id", unique=true),
	@Index(name="idx_access_rule_id", columnList="access_rule_id", unique=false)
})
public class AccessControlList {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonIgnoreProperties("accessControlList")
	private AccessRule accessRule;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Action actionSuccess;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Action actionFailed;
	
	@Column(nullable=false)
	private boolean enabled;
	
	@Column(nullable=false)
	private int priority;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private List list;

	@Column(nullable=true)
	private Integer ipResetTime;

	@Column(nullable=true)
	private String message;

	/**
	 * The list of mappings of configured status responses from the provider to responses to callers
	 * EG. provider respond with REVIEW status and the mapping is REVIEW -> ACCEPT. ACCEPT is returned to caller.
	 */
	@JoinColumn(nullable = true)
	@OneToMany
	@JsonManagedReference
	private java.util.List<AccessControlListRuleStatusOptionConfig> accessControlListRuleStatusOptionConfigList;
}
