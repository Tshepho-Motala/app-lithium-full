package lithium.service.access.provider.sphonic.data.objects;

import lombok.Data;

@Data
public class AuthenticationConfiguration {
	private String authenticationUrl;
	private String username;
	private String password;
	private Integer expirationDelay;
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
}
