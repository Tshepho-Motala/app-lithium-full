package lithium.service.user.provider.vipps.config;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Config implements Serializable {
	BASE_URL("baseUrl"),
	LOGIN_CLIENT_ID("loginClientId"),
	LOGIN_CLIENT_SECRET("loginClientSecret"),
	LOGIN_SERIAL_NUMBER("loginSerialNumber"),
	LOGIN_CALLBACK_PREFIX("loginCallbackPrefix"),
	LOGIN_CONSENT_REMOVE_PREFIX("loginConsentRemovePrefix"),
	LOGIN_FALLBACK_URL("loginFallbackUrl"),
	SUBSCRIPTION_KEY_ACCESS_TOKEN("subscriptionKeyAccessToken"),
	SUBSCRIPTION_KEY_LOGIN("subscriptionKeyLogin");
	
	@Getter
	@Accessors(fluent = true)
	private String property;
}