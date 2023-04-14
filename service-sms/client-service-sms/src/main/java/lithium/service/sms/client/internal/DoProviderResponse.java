package lithium.service.sms.client.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DoProviderResponse {
	private Long smsId;
	String providerReference;
	private DoProviderResponseStatus status;
	private String message;
	String providerCode;
}