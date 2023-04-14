package lithium.service.sms.client.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DoProviderResponseStatus {
	SUCCESS(0),
	FAILED(1),
	PENDING(2);

	@Getter Integer code;
}