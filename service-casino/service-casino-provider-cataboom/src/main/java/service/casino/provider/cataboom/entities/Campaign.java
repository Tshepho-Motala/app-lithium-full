package service.casino.provider.cataboom.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="campaign")
public class Campaign {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;	
	@Column( name="domain_name", nullable=true)
	private String domainName;
	@Column( name="token", nullable=false)
	private String token;
	@Column(nullable=false)
	private Boolean enabled;
	@Column( name="campaign_username", nullable=false)
	private String campaignUsername;
	@Column( name="campaign_password", nullable=false)
	private String campaignPassword;
	@Column( name="campaign_name", nullable=false)
	private String campaignName;
	
	@PrePersist
	public void prePersist() {
		if (enabled == null) enabled = false;
	}
	
}