package lithium.service.games.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GameUserStatus implements Serializable {
	private static final long serialVersionUID = 5919048764297420809L;
	private Long id;
	private User user;
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