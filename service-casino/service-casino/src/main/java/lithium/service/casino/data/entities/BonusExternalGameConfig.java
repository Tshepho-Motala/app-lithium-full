package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"bonusRevision"})
@EqualsAndHashCode(exclude={"bonusRevision"})
/**
 * The configuration that is required to make a call to one of the games
 * providers and receive an outcome when a player activates a bonus.
 * Populated during the bonus configuration stage.
 */
public class BonusExternalGameConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String provider;

	// TODO: 2019/08/22 Add a key-value store for external action execution parameters.
	// This is a very specific parameter and a better thing to do would be to put a system in place to allow the provider to
	// have something similar to the provider config properties but for an external action execution
	// So a key-value store to be used by the provider when the bonus is activated and the external action trigger is called.
	// No time for that now though, so will do it in iteration X
	private  Long campaignId;

	@JsonBackReference("bonusRevision")
	@ManyToOne
	private BonusRevision bonusRevision;
}
