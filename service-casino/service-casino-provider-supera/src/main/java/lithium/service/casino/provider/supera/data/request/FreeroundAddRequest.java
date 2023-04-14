package lithium.service.casino.provider.supera.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

import lithium.service.casino.provider.supera.data.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class FreeroundAddRequest extends BaseRequest {
	private String gameId;
	private String remoteId;
	private Integer betId;
	private Integer count;
	private Integer timeToLast;
	private DateTime validFrom;
	private DateTime validTo;
	
	@Override
	public Map<String, String> parameters() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getGameId() != null)
			map.put("game_id", getGameId());
		if (getRemoteId() != null)
			map.put("remote_id", getRemoteId());
		if (getBetId() != null)
			map.put("bet_id", String.valueOf(getBetId()));
		if (getCount() != null)
			map.put("count", String.valueOf(getCount()));
		if (getTimeToLast() != null)
			map.put("ttl", String.valueOf(getTimeToLast()));
		if (getValidFrom() != null)
			map.put("valid_from", String.valueOf(getValidFrom()));
		if (getValidTo() != null)
			map.put("valid_to", String.valueOf(getValidTo()));
		return map;
	}
}