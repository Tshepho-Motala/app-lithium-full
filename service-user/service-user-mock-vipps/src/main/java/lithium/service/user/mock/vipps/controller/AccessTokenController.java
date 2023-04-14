package lithium.service.user.mock.vipps.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lithium.service.user.mock.vipps.service.MockService;
import lithium.service.user.provider.vipps.domain.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value="/accessToken")
public class AccessTokenController {
	@Autowired
	private MockService mockService;

	@RequestMapping("/get")
	@ResponseBody
	public AccessTokenResponse get(
		WebRequest request
	) {
		log.info("get");
		String clientId = request.getHeader("client_id");
		String clientSecret = request.getHeader("client_secret");
		String subscriptionKey = request.getHeader("Ocp-Apim-Subscription-Key");
		
		if (mockService.validAccessTokenRequestHeaders(clientId, clientSecret, subscriptionKey)) {
			long nowMillis = System.currentTimeMillis();
			String accessToken = mockService.createJWT();
			
			AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
			.tokenType("Bearer")
			.expiresIn(100)
			.extraExpiryIn(0)
			.expiresOn(nowMillis)
			.notBefore(nowMillis)
			.resource(UUID.randomUUID().toString())
			.accessToken(accessToken)
			.build();
			
			return accessTokenResponse;
		} else {
			AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder().build();
			accessTokenResponse.setError("unauthorized_client");
			accessTokenResponse.setErrorDescription("Wrong headers supplied.");
			accessTokenResponse.setTraceId(UUID.randomUUID().toString());
			accessTokenResponse.setCorrelationId(UUID.randomUUID().toString());
			return accessTokenResponse;
		}
	}
}