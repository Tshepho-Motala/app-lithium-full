package lithium.service.affiliate.provider.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class BrandRevision implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@Column(nullable=false)
	private String displayName;

	@Column(nullable=true)
	private String url;
	
	//The affiliate will be bound to the brand contract on first use, thus creating an affiliateContract and broker contract
	//The affiliate contract is bound to a specific contract revision
	@ManyToOne
	@JoinColumn
	private Contract currentAffiliateContract;
	
	@ManyToOne
	@JoinColumn
	private Contract currentBrokerContract;

}
