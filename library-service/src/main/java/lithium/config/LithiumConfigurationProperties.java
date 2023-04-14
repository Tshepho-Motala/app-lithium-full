package lithium.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix="lithium")
@Data
@ToString
public class LithiumConfigurationProperties {
	public String gatewayPublicUrl;
	private Leader leader = new Leader();
	private Shards shards = new Shards();
	private List<ExternalApiAuthorization> externalApiAuthorizations = new ArrayList<>();

	@Data
	@ToString
	public class Leader {
		private int heartbeatMs = 30000;
	}

	@Data
	@ToString
	public class Shards {
		private int heartbeatMs = 120000;
	}

	@Data
	public static class ExternalApiAuthorization {
		private String id;
		private String secretKey;
	}
}
