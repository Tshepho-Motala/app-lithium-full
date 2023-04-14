package lithium.service.user.mock.vipps.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lithium.service.user.mock.vipps.config.VippsMockConfigurationProperties;
import lithium.service.user.mock.vipps.domain.LoginForm;
import lithium.service.user.provider.vipps.domain.Address;
import lithium.service.user.provider.vipps.domain.CallbackRequest;
import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lithium.service.user.provider.vipps.domain.ErrorInfo;
import lithium.service.user.provider.vipps.domain.UserDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MockService {
	@Autowired
	@Qualifier("lithium.service.user.mock.vipps.resttemplate")
	private RestTemplate restTemplate;
	@Autowired
	private VippsMockConfigurationProperties properties;
	
	public boolean validAccessTokenRequestHeaders(String clientId, String clientSecret, String subscriptionKey) {
		if (!properties.getAccessTokenClientId().equals(clientId)) return false;
		if (!properties.getAccessTokenClientSecret().equals(clientSecret)) return false;
		if (!properties.getOcpApimSubscriptionKey().equals(subscriptionKey)) return false;
		return true;
	}
	
	public String getUrl() {
		return properties.getUrl();
	}
	
	public boolean validLoginRequestHeaders(String authorization, String xRequestId, String xTimeStamp, String contentType, String subscriptionKey) {
		if (authorization==null || authorization.isEmpty()) return false;
		if (xRequestId==null || xRequestId.isEmpty()) return false;
		if (xTimeStamp==null || xTimeStamp.isEmpty()) return false;
		if (contentType==null || contentType.isEmpty()) return false;
		if (subscriptionKey==null || subscriptionKey.isEmpty()) return false;
		return true;
	}
	
	public void doCallbackRequestAllow(LoginForm loginForm) {
		doCallbackRequest(loginForm, true);
	}
	public void doCallbackRequestDeny(LoginForm loginForm) {
		doCallbackRequest(loginForm, false);
	}
	public void doCallbackRequestRevoke(LoginForm loginForm) {
		String url = loginForm.getConsentRemovalPrefix()+"/v2/consents/"+loginForm.getUsername();
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		final HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
		log.info(""+response);
	}
	private void doCallbackRequest(LoginForm loginForm, Boolean allow) {
		String url = loginForm.getCallbackPrefix()+"/v1/userDetails";
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set("Authorization", UUID.randomUUID().toString());
		
		CallbackRequest request = null;
		if (allow) {
			request = CallbackRequest.builder()
			.requestId(loginForm.getRequestId())
			.status(Status.SUCCESS)
			.token("")
			.userDetails(
				UserDetails.builder()
				.userId(loginForm.getUsername())
				.firstName(loginForm.getFirstname())
				.lastName(loginForm.getLastname())
				.email(loginForm.getEmail())
				.mobileNumber(loginForm.getMobile())
				.dateOfBirth(loginForm.getDateOfBirth())
				.ssn("")
				.address(
					Address.builder()
					.addressLine1("addressLine1")
					.addressLine2("addressLine2")
					.build()
				)
				.build()
			)
			.build();
		} else {
			request = CallbackRequest.builder()
			.requestId(loginForm.getRequestId())
			.status(Status.DECLINED)
			.token("")
			.userDetails(
				UserDetails.builder()
				.userId(loginForm.getUsername())
				.firstName(loginForm.getFirstname())
				.lastName(loginForm.getLastname())
				.email(loginForm.getEmail())
				.mobileNumber(loginForm.getMobile())
				.build()
			)
			.errorInfo(
				ErrorInfo.builder()
				.errorCode("403")
				.errorMessage("Login Denied!")
				.build()
			)
			.build();
		}
		
		final HttpEntity<?> requestEntity = new HttpEntity<CallbackRequest>(request, headers);
		
		restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
	}
	
	public String createJWT() {
		String id = UUID.randomUUID().toString();
		String issuer = "service-user-mock-vipps";
		String subject = "service-user-provider-vipps";
		long ttlMillis = 24 * 60 * 60 * 1000;
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(Base64.getEncoder().encodeToString(properties.getAccessTokenClientSecret().getBytes()));
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
		long expMillis = nowMillis + ttlMillis;
		
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
		.setId(id)
		.setIssuedAt(now)
		.setSubject(subject)
		.setIssuer(issuer)
		.setExpiration(new Date(expMillis))
		.signWith(signatureAlgorithm, signingKey);
		
		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}
}