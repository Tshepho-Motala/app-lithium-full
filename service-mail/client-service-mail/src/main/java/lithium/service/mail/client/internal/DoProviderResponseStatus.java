package lithium.service.mail.client.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DoProviderResponseStatus {
	SUCCESS(0),
	FAILED(1);

	@Getter Integer code;
}