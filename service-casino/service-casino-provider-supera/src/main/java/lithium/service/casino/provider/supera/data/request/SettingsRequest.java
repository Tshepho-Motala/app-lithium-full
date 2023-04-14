package lithium.service.casino.provider.supera.data.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lithium.service.casino.provider.supera.data.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class SettingsRequest extends BaseRequest {
	private Integer sessionExpire;
	private Integer sessionStartExpire;
	private String media;
	private String service;
	
	@Override
	public Map<String, String> parameters() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getSessionExpire() != null)
			map.put("session_expire", String.valueOf(getSessionExpire()));
		if (getSessionStartExpire() != null)
			map.put("session_start_expire", String.valueOf(getSessionStartExpire()));
		if (getMedia() != null)
			map.put("media", getMedia());
		if (getService() != null)
			map.put("service", getService());
		return map;
	}
}