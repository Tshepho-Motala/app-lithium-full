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
public class FreeroundRemoveRequest extends BaseRequest {
	private String gameId;
	private String remoteId;
	private Integer freeroundSetupId;
	
	@Override
	public Map<String, String> parameters() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getGameId() != null)
			map.put("game_id", getGameId());
		if (getRemoteId() != null)
			map.put("remote_id", getRemoteId());
		if (getFreeroundSetupId() != null)
			map.put("id", String.valueOf(getFreeroundSetupId()));
		return map;
	}
}