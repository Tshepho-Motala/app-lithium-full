package lithium.service.pushmsg.client.internal;

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
public class DoProviderResponse {
	private String message;
	private String providerId;
	private String providerRecipients;
	private String providerExternalId;
	private Boolean failed;
}