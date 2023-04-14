package lithium.service.domain.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@ToString(exclude="domain")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="domain")
@Table(indexes = {
	@Index(name="idx_domain_id", columnList="domain_id", unique=false),
	@Index(name="idx_domain_code", columnList="domain_id, code", unique=true),
	@Index(name="idx_domain_guid", columnList="guid", unique=true)
})
public class ProviderAuthClient implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne
	@JsonBackReference
	private Domain domain;
	
	private Date creationDate;

	private String code; // (unique per domain, machine code)
	private String description;
	private String password;
	private String guid; // (generated from domain and code)


	@PrePersist
	public void defaults() {
		if (creationDate == null) creationDate = new Date();
		if (guid == null) guid = domain.getName().toLowerCase()+"/"+code.toLowerCase();
	}

	public void setGuid() {
		guid = domain.getName().toLowerCase()+"/"+code.toLowerCase();
	}
}
