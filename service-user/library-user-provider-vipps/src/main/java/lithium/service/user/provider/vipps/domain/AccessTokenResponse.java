package lithium.service.user.provider.vipps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AccessTokenResponse extends ErrorResponse {
	@JsonProperty("token_type")
	String tokenType;       //	"token_type": "Bearer"
	@JsonProperty("expires_in")
	Integer expiresIn;      //	"expires_in": "86398",
	@JsonProperty("ext_expires_in")
	Integer extraExpiryIn;  //	"ext_expires_in": "0",
	@JsonProperty("expires_on")
	Long expiresOn;      //	"expires_on": "1495271273",
	@JsonProperty("not_before")
	Long notBefore;      //	"not_before": "1495184574",
	@JsonProperty("resource")
	String resource;        //	"resource": "00000002-0000-0000-c000-000000000000",
	@JsonProperty("access_token")
	String accessToken;     //	"access_token": ""
}
