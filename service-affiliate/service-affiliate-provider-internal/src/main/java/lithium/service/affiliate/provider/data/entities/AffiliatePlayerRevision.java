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
		@Index(name="idx_affiliate_player_id", columnList="affiliatePlayerId"),
		@Index(name="idx_affiliate_id", columnList="affiliate_id"),
	})

public class AffiliatePlayerRevision {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;
	
	@ManyToOne
	@JoinColumn
	private Affiliate affiliate;
	
	private long affiliatePlayerId;

	private Date effectiveDate;
	
	private Date archiveDate;
	
	private String primaryGuid; //usually affiliate guid (this might not be required as we have a link to affiliate in this entity)
	
	private String secondaryGuid; //usually banner guid
	
	private String tertiaryGuid;
	
	private String quaternaryGuid;
	
	private boolean current;
	
	@PrePersist
	public void defaults() {
		if (effectiveDate == null) effectiveDate = new Date();
	}
}
