package lithium.service.affiliate.provider.data.entities;

import javax.persistence.Column;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;

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
@Table(indexes={
		@Index(name="idx_guid", columnList="guid", unique=true)
})
public class Campaign {
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private String guid;
	
	@ManyToOne
	@JoinColumn
	private CampaignRevision current;

	@ManyToOne
	@JoinColumn
	private CampaignRevision edit;
	
	@PrePersist
	void defaults() {
		if (guid == null) guid = Hex.encodeHexString(RandomStringUtils.randomAlphanumeric(40).getBytes());
	}

}
