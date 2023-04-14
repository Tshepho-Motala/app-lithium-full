package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
/**
 * Contains the outcome of a call to an external bonus games
 * provider in the form of a URL to be executed by the player on the frontend.
 *
 * Should be populated during the player bonus allocation phase
 */
public class PlayerBonusExternalGameLink {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	String externalGameUrl;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private PlayerBonusHistory playerBonusHistory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private BonusExternalGameConfig bonusExternalGameConfig;
}
