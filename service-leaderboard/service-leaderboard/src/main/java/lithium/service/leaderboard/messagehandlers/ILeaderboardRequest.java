package lithium.service.leaderboard.messagehandlers;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ILeaderboardRequest {
	@NotNull
	private String playerGuid;
	
	/// Utility methods
	public String domainName() {
		return playerGuid.split("/")[0];
	}
	public String username() {
		return playerGuid.split("/")[1];
	}
}