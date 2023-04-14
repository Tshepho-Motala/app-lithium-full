package lithium.service.casino.client.objects;

import lombok.Getter;

/**
 * Enum for possible responses from provider implementations to generic mock service.
 */
public enum EMockResponseStatus {
	SUCCESS ("SUCCESS"),
	FAIL ("FAIL"),
	SUCCESS_DUPLICATE ("SUCCESS_DUPLICATE"),
	ERROR ("ERROR"),
	UNIMPLEMENTED ("UNIMPLEMENTED");

	@Getter
	private String status;

	EMockResponseStatus(final String status) {
		this.status = status;
	}
}
