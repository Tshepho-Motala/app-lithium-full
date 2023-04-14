package lithium.service.games.client.objects;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class GameStream {
	private Game game;
	private GameGraphic graphic;
}
