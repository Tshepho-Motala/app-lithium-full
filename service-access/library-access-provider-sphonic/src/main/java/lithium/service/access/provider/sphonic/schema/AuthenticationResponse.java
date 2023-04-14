package lithium.service.access.provider.sphonic.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	@JsonProperty("access_token")
	private String accessToken;
	private String scope;
	@JsonProperty("expires_in")
	private Long expiresIn;
	@JsonProperty("token_type")
	private String tokenType;
}
