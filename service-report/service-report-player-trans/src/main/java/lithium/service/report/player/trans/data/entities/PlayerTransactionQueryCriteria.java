package lithium.service.report.player.trans.data.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
		@Index(name="idx_tq_hash", columnList="hash", unique=true)
})
public class PlayerTransactionQueryCriteria {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;
	
	private String userGuid;
	
	private Date startDate;
	
	private Date endDate;
	
	private String hash;
	
	private Date createdDate;
	
	private Date completedDate;
	
	private boolean dataPurged;
}
