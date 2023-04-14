package lithium.service.access.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lithium.service.access.client.objects.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.List;

@Data
@Table
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExternalList {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonIgnoreProperties("externalList")
	private AccessRule accessRule;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Action actionSuccess;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Action actionFailed;
	
	@Column(nullable=false)
	private String providerUrl;
	
	@Column(nullable=false)
	private String listName;
	
	@Column(nullable=false)
	private boolean enabled;

	@Column(nullable = false)
	private int priority;

	@Column(nullable = false)
	private boolean validateOnce;

	@Column(nullable=true)
	private String message; //rejectMessage

  @Column()
  private String timeoutMessage;
  
  @Column()
  private String reviewMessage;

	/**
	 * The list of mappings of configured status responses from the provider to responses to callers
	 * EG. provider respond with REVIEW status and the mapping is REVIEW -> ACCEPT. ACCEPT is returned to caller.
	 */
	@JoinColumn(nullable = true)
	@OneToMany
	@JsonManagedReference
	private List<ExternalListRuleStatusOptionConfig> externalListRuleStatusOptionConfigList;
}
