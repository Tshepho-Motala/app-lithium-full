package lithium.service.games.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public abstract class GameResponseBasic {
	private String gameId;
	private String gameName;
	private String commercialGameName;
	private String image;
	private String supplierName;
	private Boolean freeGame;
	private String gameStudioName;
}
