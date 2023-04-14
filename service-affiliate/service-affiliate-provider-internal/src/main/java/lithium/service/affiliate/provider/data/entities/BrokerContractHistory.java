package lithium.service.affiliate.provider.data.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
//@Table(indexes = {
//		@Index(name="idx_userguid", columnList="userGuid", unique=true),
//		@Index(name="idx_guid", columnList="guid", unique=true),
//	})

public class BrokerContractHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	private Contract contract;
	
	private boolean current;
	
	private Date startDate;
	
	private Date endDate;
	
	@JsonBackReference("currentBrokerContract")
	@ManyToOne
	private Affiliate affiliate;
}
