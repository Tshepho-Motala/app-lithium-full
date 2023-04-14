package lithium.service.access.provider.iovation.config;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Config implements Serializable {
	BASE_URL("baseUrl"),
	SUBSCRIBER_ID("subscriberId"),
	SUBSCRIBER_ACCOUNT("subscriberAccount"),
	SUBSCRIBER_PASSCODE("subscriberPasscode"),
	CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
	CONNECT_TIMEOUT("connectTimeout"),
	SOCKET_TIMEOUT("socketTimeout");
	
	@Getter
	@Accessors(fluent = true)
	private String property;
}