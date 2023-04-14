package lithium.service.games.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainGameData {
	String lobbyOpenUrl;
	String gameStartUrl;
	String imageUrl;
	String demoUrl;
	Iterable<Game> list;

}
