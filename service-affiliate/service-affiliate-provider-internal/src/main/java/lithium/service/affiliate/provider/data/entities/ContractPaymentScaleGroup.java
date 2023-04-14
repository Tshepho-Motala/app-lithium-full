package lithium.service.affiliate.provider.data.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@Table(indexes = {
@Index(name="idx_contract_payment_scale", columnList="contract_id", unique=false)
})
public class ContractPaymentScaleGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	private Concept granularityConcept; //used in program to specify granularity for the lookup of limit value.
	
	private int granularityOffset; //Specify how far back the granularity concept implementation query should look into the past. 
	
	@OneToMany
	private List<PaymentScale> paymentScaleList;
	
	@ManyToOne
	@JsonBackReference
	private Contract contract;
	
	@ManyToOne
	private Concept concept; // used to match a concept passed from the remote system. Overrides the default concept assigned to each contract
}
