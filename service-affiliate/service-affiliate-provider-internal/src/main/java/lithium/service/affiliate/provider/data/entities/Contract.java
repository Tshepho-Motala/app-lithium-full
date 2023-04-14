package lithium.service.affiliate.provider.data.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
//@Table(indexes = {
//		@Index(name="idx_contract_concept", columnList="contract_id, concept_id", unique=true)
//	})

public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	private String name;
	
	private ContractType type;
	
	private Date creationDate;
	
	private Concept batchRunFrequency;
	
	@ManyToOne
	@JoinColumn
	private ContractRevision current;
	
	@ManyToOne
	@JoinColumn
	private ContractRevision edit;
	
	private List<ContractPaymentScaleGroup> payScaleGroupList;
	
	@PrePersist
	void defaults() {
		if (creationDate == null) creationDate = new Date();
	}
}
