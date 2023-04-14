package lithium.service.games.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RecentlyPlayedGameBasic extends GameResponseBasic {
	private Date lastUsed;

	@Builder
	public RecentlyPlayedGameBasic(String gameId, String gameName, String commercialGameName, String image, String supplierName, boolean freeGame, String gameStudioName, Date lastUsed) {
		super(gameId, gameName, commercialGameName, image, supplierName, freeGame, gameStudioName);
		this.lastUsed = lastUsed;
	}
}
