package lithium.service.user.provider.vipps.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lithium.service.user.provider.vipps.config.Config;
import lithium.service.user.provider.vipps.config.VippsConfigurationProperties;
import lithium.service.user.provider.vipps.domain.AccessTokenResponse;
import lithium.service.user.provider.vipps.domain.AuthAttempt;
import lithium.service.user.provider.vipps.domain.CallbackRequest;
import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lithium.service.user.provider.vipps.domain.ErrorInfo;
import lithium.service.user.provider.vipps.domain.LoginDetailsResponse;
import lithium.service.user.provider.vipps.domain.MerchantInfo;
import lithium.service.user.provider.vipps.domain.SignupOrLoginRequest;
import lithium.service.user.provider.vipps.domain.SignupOrLoginResponse;
import lithium.service.user.provider.vipps.domain.UserDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VippsService {
	@Autowired
	private VippsConfigurationProperties vippsConfig;
	@Autowired
	private LithiumServiceClientFactory serviceFactory;
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AuthAttemptService authAttemptService;
	@Autowired
	@Qualifier("lithium.service.user.provider.vipps.resttemplate")
	private RestTemplate restTemplate;
	@Getter
	@Value("${spring.application.name}")
	private String moduleName;
	
	public void svcUserSaveUser(String domainName, CallbackRequest callbackRequest) throws Exception {
		UserClient userClient = serviceFactory.target(UserClient.class);
		Map<String, String> map = new HashMap<>();
		map.put("external-token", callbackRequest.getRequestId());
		map.put("external-username", callbackRequest.getUserDetails().getUserId());
		Response<User> userResponse = userClient.user(domainName, callbackRequest.getUserDetails().getMobileNumber(), map);
		log.debug("Response :: "+userResponse);
	}
	
	public void svcUserDeleteUser(String domainName, String userId) throws Exception {
		UserClient userClient = serviceFactory.target(UserClient.class);
		Map<String, String> map = new HashMap<>();
		map.put("u", userId);
		Response<String> userResponse = userClient.delete(domainName, map);
		log.debug("Response :: "+userResponse);
	}
	
	public Map<String, Object> fallback(String domainName, String xRequestId, HttpServletResponse response) throws Exception {
		Map<String, Object> oauth = new HashMap<>();
		String acme = Base64.getEncoder().encodeToString("acme:acmesecret".getBytes());
		
		AuthAttempt authAttempt =  authAttemptService.findAuthAttemptByXRequestId(xRequestId);
		UserDetails userDetails = authAttempt.getCallbackRequest().getUserDetails();
		
		final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("grant_type", "password");
		parts.add("domain", domainName);
		parts.add("username", userDetails.getMobileNumber());
		parts.add("password", authAttemptService.passwordGen(xRequestId));
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization", "Basic " + acme);
		headers.set("external-token", xRequestId);
		
		final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
		
		ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(config.getGatewayPublicUrl()+"/server-oauth2/oauth/token", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});
		log.debug("ResponseEntity :: "+responseEntity);
		
		oauth = responseEntity.getBody();
		
		log.debug("token :: "+oauth);
		if (!authAttempt.success()) oauth.put("ErrorInfo", authAttempt.getCallbackRequest().getErrorInfo());
		return oauth;
	}
	
	public boolean validIp(String remoteAddr) {
		log.debug("Check ip whitelist for : "+remoteAddr);
		boolean whitelisted = false;
		List<String> ips = vippsConfig.getAllowedIPs();
		for (String ip:ips) {
			log.debug(ip+" : "+(remoteAddr.equalsIgnoreCase(ip)));
			if (remoteAddr.equalsIgnoreCase(ip)) whitelisted = true;
		};
		log.debug("whitelisted : "+whitelisted);
		return whitelisted;
	}
	
	public boolean accessTokenValid(String accessToken) {
		String validAccessToken = vippsConfig.getApiToken();
		if (accessToken.equals(validAccessToken)) return true;
		return false;
	}
	
	private AccessTokenResponse accessToken(Provider provider) {
		String clientId = provider.propertyValueOrDefault(Config.LOGIN_CLIENT_ID.property(), "");
		String clientSecret = provider.propertyValueOrDefault(Config.LOGIN_CLIENT_SECRET.property(), "");
		String accessTokenUrl = provider.propertyValueOrDefault(Config.BASE_URL.property(), "")+"/accessToken/get";
		String subscriptionKey = provider.propertyValueOrDefault(Config.SUBSCRIPTION_KEY_ACCESS_TOKEN.property(), "");
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set("client_id", clientId);
		headers.set("client_secret", clientSecret);
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
		log.debug("AccessToken HttpHeaders : "+headers);
		
		final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(new LinkedMultiValueMap<String, Object>(), headers);
		
		ResponseEntity<AccessTokenResponse> accessTokenResponse = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, requestEntity, AccessTokenResponse.class);
		log.debug("AccessTokenResponse : "+accessTokenResponse);
		if (accessTokenResponse.getBody()!=null) return accessTokenResponse.getBody();
		return null;
	}
	
	private SignupOrLoginResponse signupOrLogin(String xRequestId, String domainName, AccessTokenResponse accessToken, Provider provider, String returnAddr) throws UnsupportedEncodingException {
		long time = DateTime.now().getMillis();
		final HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set("Authorization", accessToken.getTokenType()+" "+accessToken.getAccessToken());
		headers.set("X-Request-Id", xRequestId);
		headers.set("X-TimeStamp", ""+time);
		headers.set("X-App-Id", provider.propertyValueOrDefault(Config.LOGIN_CLIENT_ID.property(), ""));
		headers.set("Ocp-Apim-Subscription-Key", provider.propertyValueOrDefault(Config.SUBSCRIPTION_KEY_LOGIN.property(), ""));
		log.debug("SignupOrLogin HttpHeaders : "+headers);
		
		MerchantInfo merchantInfo = MerchantInfo.builder()
		.merchantSerialNumber(provider.propertyValueOrDefault(Config.LOGIN_SERIAL_NUMBER.property(), ""))
		.callbackPrefix(provider.propertyValueOrDefault(Config.LOGIN_CALLBACK_PREFIX.property(), "")+"/"+domainName)
		.consentRemovalPrefix(provider.propertyValueOrDefault(Config.LOGIN_CONSENT_REMOVE_PREFIX.property(), "")+"/"+domainName+"/"+vippsConfig.getApiToken())
		.fallBack(provider.propertyValueOrDefault(Config.LOGIN_FALLBACK_URL.property(), "")+"/"+domainName+"/"+xRequestId+"/fallback")
		.autoLoginToken("")
		.isApp(false)
		.build();
//		if (!returnAddr.isEmpty()) merchantInfo.setFallBack(URLEncoder.encode(returnAddr+"&rid="+rid, "UTF-8"));
		if ((returnAddr!=null)&&(returnAddr.indexOf("?")!=-1)) returnAddr += "&"; else returnAddr += "?";
		if ((returnAddr!=null)&&(!returnAddr.isEmpty())) merchantInfo.setFallBack(returnAddr+"auth="+xRequestId);
		log.debug("SignupOrLogin MerchantInfo : "+merchantInfo);
		
		SignupOrLoginRequest request = SignupOrLoginRequest.builder().merchantInfo(merchantInfo).build();
		
		final HttpEntity<?> requestEntity = new HttpEntity<SignupOrLoginRequest>(request, headers);
		
		String url = provider.propertyValueOrDefault(Config.BASE_URL.property(), "")+"/signup/v1/loginRequests";
		log.debug("SignupOrLogin POST : "+url);
		log.debug("SignupOrLogin HttpEntity : "+requestEntity);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		log.debug("ResponseEntity : "+response.getBody());
		SignupOrLoginResponse signupOrLoginResponse = SignupOrLoginResponse.builder().build();
		try {
			signupOrLoginResponse = objectMapper.readValue(response.getBody(), SignupOrLoginResponse.class);
			log.debug("SignupOrLoginResponse : "+signupOrLoginResponse);
		} catch (HttpMessageNotReadableException | IOException e) {
			log.warn("Could not read SignupOrLoginResponse, will try to parse error response now.");
			try {
				@SuppressWarnings("unchecked")
				Map<String, String>[] errors = objectMapper.readValue(response.getBody(), Map[].class);
				log.trace("error : "+errors);
				for (Map<String, String> error:errors) {
					signupOrLoginResponse.addErrorDetail(error.get("errorCode"), error.get("errorMessage"));
				}
			} catch (IOException e1) {
				log.error(e.getMessage(), e1);
			}
		}
		log.debug("SignupOrLoginResponse : "+signupOrLoginResponse);
		return signupOrLoginResponse;
	}
	
	public CallbackRequest loginRequestDetails(String domainName, String url, String xRequestId) throws Exception {
		Provider provider = providerAuth(domainName);
		
		AccessTokenResponse accessToken = accessToken(provider);
		if (accessToken != null) {
			long time = DateTime.now().getMillis();
			final HttpHeaders headers = new HttpHeaders();
			
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set("Authorization", accessToken.getTokenType()+" "+accessToken.getAccessToken());
			headers.set("X-Request-Id", xRequestId);
			headers.set("X-TimeStamp", ""+time);
			headers.set("X-App-Id", provider.propertyValueOrDefault(Config.LOGIN_CLIENT_ID.property(), ""));
			headers.set("Ocp-Apim-Subscription-Key", provider.propertyValueOrDefault(Config.SUBSCRIPTION_KEY_LOGIN.property(), ""));
			log.debug("HttpHeaders : "+headers);
			final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(new LinkedMultiValueMap<String, Object>(), headers);
			
			ResponseEntity<String> responseEntity = restTemplate.exchange(url+xRequestId, HttpMethod.GET, requestEntity, String.class);
			log.debug("LoginDetailsResponse:"+responseEntity);
			if (responseEntity.getBody()==null) return null;
			
			LoginDetailsResponse response = LoginDetailsResponse.builder().build();
			try {
				response = objectMapper.readValue(responseEntity.getBody(), LoginDetailsResponse.class);
				log.debug("LoginDetailsResponse : "+response);
			} catch (HttpMessageNotReadableException | IOException e) {
				log.warn("Could not read LoginDetailsResponse, will try to parse error response now.");
				try {
					@SuppressWarnings("unchecked")
					Map<String, String>[] errors = objectMapper.readValue(responseEntity.getBody(), Map[].class);
					log.trace("error : "+errors);
					for (Map<String, String> error:errors) {
						response.addErrorDetail(error.get("errorCode"), error.get("errorMessage"));
					}
				} catch (IOException e1) {
					log.error(e.getMessage(), e1);
				}
			}
			return CallbackRequest.builder()
			.status(response.getStatus())
			.requestId(response.getRequestId())
			.userDetails(response.getUserDetails())
			.errorInfo((response.getErrorDetails()!=null)?ErrorInfo.builder().errorCode(response.firstErrorCode()).errorMessage(response.firstErrorMessage()).build():null)
			.build();
		}
		return null;
	}
	
	public SignupOrLoginResponse auth(String domainName, String returnAddr) throws Exception {
		Provider provider = providerAuth(domainName);
		log.debug("auth attempt using provider : "+provider);
		log.debug("returnAddress : "+returnAddr);
		
		AuthAttempt authAttempt = authAttemptService.generateAndSave();
		
		AccessTokenResponse accessToken = accessToken(provider);
		if (accessToken != null) {
			authAttempt.setExpiresOn(new DateTime(accessToken.getExpiresOn()));
			authAttempt.setProvidedAccessToken(accessToken.getAccessToken());
			authAttempt = authAttemptService.saveAuthAttempt(authAttempt);
			SignupOrLoginResponse signupOrLoginResponse = signupOrLogin(authAttempt.getXRequestId(), domainName, accessToken, provider, returnAddr);
			authAttempt.setUrl(signupOrLoginResponse.getUrl());
			authAttempt.setCallbackRequest(
				CallbackRequest.builder()
				.status(Status.NEW)
				.requestId(signupOrLoginResponse.getRequestId())
				.build()
			);
			authAttempt = authAttemptService.saveAuthAttempt(authAttempt);
			return signupOrLoginResponse;
		}
		return null;
	}
	
	public Provider providerAuth(String domainName) throws Exception {
		return provider(domainName, ProviderType.AUTH);
	}
	
	private Provider provider(String domainName, ProviderType providerType) throws Exception {
		ProviderClient providerClient = serviceFactory.target(ProviderClient.class, true);
		Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, providerType.type());
		List<Provider> providers = new ArrayList<>();
		
		if (response.isSuccessful()) {
			response.getData().forEach(providers::add);
			providers.removeIf(p -> (p.getEnabled() == false && !p.getUrl().equalsIgnoreCase(moduleName)));
			providers.sort(Comparator.comparingInt(Provider::getPriority));
			if (providers.size() != 1) throw new Exception("Provider Setup Issue.");
			return providers.get(0);
		}
		throw new Exception("No provider found.");
	}
}
