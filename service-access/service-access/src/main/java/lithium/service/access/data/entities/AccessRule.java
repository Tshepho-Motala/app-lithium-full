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

import lithium.service.access.client.objects.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude= {"accessControlList", "externalList"})
@Table(indexes = {
	@Index(name="idx_access_rule_id", columnList="id", unique=true),
	@Index(name="idx_domain_name", columnList="domain_id, name", unique=true)
})
public class AccessRule {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String name;

	@Column(nullable=true)
	private String description;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Action defaultAction;

	@Column(nullable=true)
	private String defaultMessage;
	
	@Column(nullable=false)
	private boolean enabled;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="accessRule")
	@JsonIgnoreProperties("accessRule")
	private java.util.List<AccessControlList> accessControlList;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="accessRule")
	@JsonIgnoreProperties("accessRule")	
	private java.util.List<ExternalList> externalList;

	public boolean hasRules() {
		if ((!accessControlList.isEmpty()) || (!externalList.isEmpty())) return true;
		return false;
	}

	public void addList(AccessControlList list) {
		if (accessControlList == null) accessControlList = Collections.emptyList();
		accessControlList.add(list);
	}
	public void addExternalList(ExternalList list) {
		if (externalList == null) externalList = Collections.emptyList();
		externalList.add(list);
	}
}
