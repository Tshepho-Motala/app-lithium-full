package lithium.service.casino.provider.roxor.api.schema.gamelist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GameListInternal {
	@JsonProperty(value="GameList", required=true)
	List<GameInternal> gameList;
}
