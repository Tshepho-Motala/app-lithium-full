package lithium.service.user.provider.vipps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class MerchantInfo {
	private String merchantSerialNumber;// "merchantSerialNumber": "100948",
	private String callbackPrefix;// "callbackPrefix": "https://www.domain.no/sign",
	private String consentRemovalPrefix;// "consentRemovalPrefix": "https://www.domain.no/consentRemoval",
	private String fallBack;// "fallBack": "https://dnsname/contextPath/api/fallback?sessionId:1234566",
	private String authToken;
	private String autoLoginToken;// "autoLoginToken":"",
	private Boolean isApp;// "isApp" : false
}
