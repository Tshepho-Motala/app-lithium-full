package lithium.service.pushmsg.provider.onesignal.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class ViewDeviceResponse {
	@JsonProperty("session_count")
	private Long sessionCount; // 5,
	@JsonProperty("language")
	private String language; //": "en",
	@JsonProperty("device_os")
	private String deviceOs; //": "72",
	@JsonProperty("device_type")
	private Integer deviceType; //": 5,
	@JsonProperty("device_model")
	private String deviceModel; //": "MacIntel",
	@JsonProperty("last_active")
	private Integer lastActive; //": 1551074028,
	@JsonProperty("created_at")
	private Integer createdAt; //": 1550764356,
	@JsonProperty("ip")
	private String ip; //: "196.22.242.138",
}