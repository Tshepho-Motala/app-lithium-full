package lithium.service.casino.provider.supera.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lithium.service.casino.provider.supera.data.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class GameRoundRequest extends BaseRequest {
	private Integer gameId;
	private Integer gameRoundId;
	
	@Override
	public Map<String, String> parameters() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getGameId() != null)
			map.put("game_id", String.valueOf(getGameId()));
		if (getGameRoundId() != null)
			map.put("gr_id", String.valueOf(getGameRoundId()));
		return map;
	}
}