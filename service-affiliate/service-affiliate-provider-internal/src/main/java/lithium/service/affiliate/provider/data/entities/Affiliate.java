package lithium.service.affiliate.provider.data.entities;

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
@Table(indexes = {
		@Index(name="idx_userguid", columnList="userGuid", unique=true),
		@Index(name="idx_guid_domain_id", columnList="guid, domain_id", unique=true),
	})
public class Affiliate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	private String userGuid;
	
	private String guid; //TODO: scenario where 2 external affiliate systems have the same guid for their affiliate identification. Unique index will fail. there are other issues with this anyway.
	
	private String additionalLinkData; // Additional identification for external affiliations. Processor impl can use this
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@PrePersist
	void defaults() {
		if (guid == null) guid = Hex.encodeHexString(RandomStringUtils.randomAlphanumeric(20).getBytes());
		if (userGuid == null) userGuid = guid;
	}

}
