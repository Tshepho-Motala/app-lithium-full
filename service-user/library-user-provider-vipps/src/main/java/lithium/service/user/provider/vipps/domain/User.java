package lithium.service.user.provider.vipps.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown=true)
@Table(indexes = {
	@Index(name="idx_u_all", columnList="domainName, userId", unique=true)
})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Builder.Default
	private DateTime created = DateTime.now();
	private String userId;
	private String domainName;
	@Builder.Default
	private Boolean deleted = Boolean.FALSE;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "current_auth_attempt_id")
	private AuthAttempt currentAuthAttempt;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "current_user_details_id")
	private UserDetails currentUserDetails;

}