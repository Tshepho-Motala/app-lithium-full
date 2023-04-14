package lithium.service.pushmsg.client.internal;

import java.util.Date;

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
public class DeviceResponse {
	private Long sessionCount; // 5,
	private String language; //": "en",
	private String deviceOs; //": "72",
	private Integer deviceType; //": 5,
	private String deviceModel; //": "MacIntel",
	private Date lastActive; //": 1551074028,
	private Date createdAt; //": 1550764356,
	private String ip; //: "196.22.242.138",
}