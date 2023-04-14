package lithium.service.access.client.objects;

import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAuthorizationRequest implements Serializable {
	private static final long serialVersionUID = 1343884971201327181L;
	private String domainName;
	private String ruleName;
	private String userGuid;
	private String deviceId; //blackbox
	private String ip;
	private Map<String, String> additionalData;
	private PlayerBasic playerBasic; // Should have possibly used additional data but the mapping would be tricky
}
