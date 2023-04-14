package lithium.service.user.client.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RestrictionsMessageType implements Serializable {
	DOMAIN_SET_UPDATE("Domain restriction update"),
	USER_SET_UPDATE("User restriction update"),
	USER_SET_DELETE("User restriction delete");

	@Getter
	@Accessors(fluent = true)
	private String description;
}
