package lithium.service.domain.data.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude="domain")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="domain")
@Table(indexes = {
	@Index(name="idx_domain_id", columnList="domain_id", unique=false)
})
public class DomainRevision implements Serializable {
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
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy="domainRevision")
	private List<DomainRevisionLabelValue> labelValueList;
	
	@PrePersist
	public void defaults() {
		if (creationDate == null) creationDate = new Date();
	}
}
