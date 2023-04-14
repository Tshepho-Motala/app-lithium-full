package lithium.service.user.provider.facebook.config;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@ConfigurationProperties(prefix = "lithium.facebook")
public class FacebookConfigurationProperties {
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum Config implements Serializable {
		APP_ID("appId"),
		APP_SECRET("appSecret");
		
		@Getter
		@Accessors(fluent = true)
		private String property;
	}
}