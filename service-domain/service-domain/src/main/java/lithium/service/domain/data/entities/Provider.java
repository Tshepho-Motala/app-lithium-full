package lithium.service.domain.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name = "idx_pt_all", columnList="domain_id, provider_type_id, url", unique=true)
})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class Provider implements Serializable {
	
	private static final long serialVersionUID = 5938339472273216181L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	
	@Builder.Default
	@Column(nullable=false)
	private Integer priority = 1;
	
	@Builder.Default
	@Column(nullable=false)
	private Boolean enabled = false;
	
	@Column(nullable=false)
//	@Size(min=2, max=35, message="No more than 30 and no less than 2 characters")
//	@Pattern(regexp="^[_a-z0-9\\.\\-]+$", message="Only numbers, lowercase letters, underscore, dashes and dots allowed")
	private String name;
	
	@Column(nullable=false, unique=false)
	private String url;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private ProviderType providerType;
	
	@JsonManagedReference("provider")
	@OneToMany(fetch = FetchType.EAGER, mappedBy="provider", cascade=CascadeType.ALL)
	private List<ProviderProperty> properties = new ArrayList<>();
	
	public boolean internal() {
		if (url.equalsIgnoreCase("internal")) return true;
		return false;
	}
}
