package lithium.service.games.client.objects;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class TaggedGameBasic extends GameResponseBasic {
	@Builder
	public TaggedGameBasic(String gameId, String gameName, String commercialGameName, String image, String supplierName,  boolean freeGame, String gameStudioName) {
		super(gameId, gameName, commercialGameName, image, supplierName, freeGame, gameStudioName);
	}
}
