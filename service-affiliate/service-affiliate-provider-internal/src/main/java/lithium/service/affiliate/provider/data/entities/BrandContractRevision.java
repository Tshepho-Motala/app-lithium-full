package lithium.service.affiliate.provider.data.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

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
		@Index(name="idx_brand_id", columnList="brandId")
	})

public class BrandContractRevision {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne
	@JoinColumn
	private Contract affiliateContract;

	@ManyToOne
	@JoinColumn
	private Contract brokerContract;
	
	private long brandId;

	private Date effectiveDate;
	
	private Date archiveDate;
	
	@PrePersist
	public void defaults() {
		if (effectiveDate == null) effectiveDate = new Date();
	}
}
