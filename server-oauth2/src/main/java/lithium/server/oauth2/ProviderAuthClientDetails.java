package lithium.server.oauth2;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.ToString;

import java.util.Collection;
import java.util.Set;

@Data
@ToString(exclude = {"clientSecret"})
@Builder(toBuilder = true)
public class ProviderAuthClientDetails {
	private String clientId;
	private String clientSecret;
	@Singular
	private Set<String> resourceIds;
	@Singular
	private Set<String> scopes;
	@Singular
	private Set<String> authorizedGrantTypes;
	@Singular
	private Collection<String> authorities;
//	@Builder.Default
//	private Integer accessTokenValiditySeconds = 50000;
//	@Builder.Default
//	private Integer refreshTokenValiditySeconds = 50000;
//	@Builder.Default
//	private boolean autoApprove = false;
//	@Builder.Default
//	private Map<String, Object> additionalInformation = new HashMap<>();
}

