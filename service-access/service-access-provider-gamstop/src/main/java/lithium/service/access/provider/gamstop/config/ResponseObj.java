package lithium.service.access.provider.gamstop.config;

import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.gamstop.data.objects.SelfExclusionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObj {
	private boolean success;
	private String type;
	private RawAuthorizationData rawAuthorizationData;
	private SelfExclusionResponse selfExclusionResponse;
}
