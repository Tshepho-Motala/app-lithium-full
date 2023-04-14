package lithium.service.casino.provider.supera.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lithium.service.casino.provider.supera.data.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class GetGameRequest extends BaseRequest {
	private String remoteId;
	private String gameId;
	private String remoteData;
	
	@Override
	public Map<String, String> parameters() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getRemoteId() != null)
			map.put("remote_id", getRemoteId());
		if (getGameId() != null)
			map.put("game_id", getGameId());
		if (getRemoteData() != null)
			map.put("remote_data", getRemoteData());
		return map;
	}
}