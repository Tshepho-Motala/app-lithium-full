package lithium.service.games.data.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(
  name = "game_user_status",
  indexes = {
	  @Index(name="idx_gm_guid_domain", columnList="user_id, game_id", unique=true)
  }
)
public class GameUserStatus implements Serializable {
	private static final long serialVersionUID = 9030222794321991496L;

	@Version
	private int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Game game;
	
	private Boolean enabled;
	private Boolean locked;
	
	/// Utility methods
	public String domainName() {
		return user.guid().split("/")[0];
	}
	public String username() {
		return user.guid().split("/")[1];
	}
	public String playerGuid() {
		return user.guid();
	}
	public String gameGuid() {
		return game.getGuid();
	}
	public Long gameId() {
		return game.getId();
	}
}
