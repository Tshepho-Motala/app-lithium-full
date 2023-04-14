package lithium.service.casino.provider.sgs.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.provider.sgs.SGSModuleInfo.ConfigProperties;
import lithium.service.casino.provider.sgs.config.APIAuthentication;
import lithium.service.casino.provider.sgs.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.sgs.data.ErrorCodes;
import lithium.service.casino.provider.sgs.data.request.Request;
import lithium.service.casino.provider.sgs.data.response.Extinfo;
import lithium.service.casino.provider.sgs.data.response.MethodResponse;
import lithium.service.casino.provider.sgs.data.response.Result;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.mail.client.MailClient;
import lithium.service.user.client.UserApiClient;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SGSService {
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	private TokenStore tokenStore;
	
	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for(ProviderProperty p: pp.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CUSTOMER_ID.getValue())) brandConfiguration.setCustomerId(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.LANGUAGE.getValue())) brandConfiguration.setLanguage(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CURRENCY.getValue())) brandConfiguration.setCurrency(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_KEY.getValue())) brandConfiguration.setApiKey(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.IMAGE_URL.getValue())) brandConfiguration.setImageUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_LOGIN.getValue())) brandConfiguration.setApiLogin(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_PASSWORD.getValue())) brandConfiguration.setApiPassword(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.MOCK_FLAG.getValue())) brandConfiguration.setMockActive(Boolean.parseBoolean(p.getValue()));
		}
		
		return brandConfiguration;
	}
	
	public CasinoClient getCasinoService() {
		CasinoClient cl = null;
		try {
			cl = services.target(CasinoClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino service", e);
		}
		
		return cl;
	}
	
	public CasinoFrbClient getCasinoFrbService() {
		CasinoFrbClient cl = null;
		try {
			cl = services.target(CasinoFrbClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino frb service", e);
		}
		
		return cl;
	}
	
	public MailClient getMailService() {
		MailClient cl = null;
		try {
			cl = services.target(MailClient.class,"service-mail", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting mail service", e);
		}
		
		return cl;
	}
	
	public ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class,"service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}
	
	public UserApiClient getUserApiService() {
		UserApiClient cl = null;
		try {
			cl = services.target(UserApiClient.class,"service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting user api service", e);
		}
		
		return cl;
	}
	
	public String getApiTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		//apiToken += "|"+util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return apiToken;
	}
//	
//	@Async
//	@Retryable(maxAttempts=3, backoff=@Backoff(delay=10000, multiplier=2))
//	public void retryFrbAllocation(AwardBonusRequest request, BrandsConfigurationBrand bc, RestTemplate restTemplate, String frbUrl, String domainName) throws Exception {
//		try {
//			
//			AwardBonusRequestResponse response = restTemplate.getForObject(
//				bc.getBaseUrl()+frbUrl+setRequestParams(request.getParamMap()),
//				AwardBonusRequestResponse.class,
//				request.getParamMap()
//			);
//			log.debug("AwardBonusRequestResponse (retry): "+response);
//			if (!(response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_SUCCESS) || response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_CODE_BONUS_CODE_ALREADY_EXISTS))) {
//				log.error("AwardBonusRequestResponse (retry): "+response);
//				try {
//					getMailService().save(SystemEmailData.builder()
//					.domainName(domainName)
//					.priority(1)
//					.subject("External bonus allocation failure (retry) for: " + request.getUserId())
//					.body("Retry of external bonus attempt failed.\r\n" + response.toString())
//					.userGuid(request.getUserId())
//					.build());
//				} catch (Exception ex) {
//					log.error("Unable to send system mail to mail service. " + ex.getMessage(), ex);
//				}
//				throw new Exception("Got bad response from provider (retry): " +  response.getResponse().getResult());
//			}
//			
//			if (response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_CODE_BONUS_CODE_ALREADY_EXISTS)) {
//				log.warn("AwardBonusRequestResponse (retry):  "+response);
//				response.getResponse().setCode(AwardBonusResponse.RESPONSE_SUCCESS);
//			}
//			
//			lithium.service.casino.client.objects.response.AwardBonusResponse finalResponse = mapper.map(response.getResponse(), lithium.service.casino.client.objects.response.AwardBonusResponse.class);
//			log.debug("AwardBonusResponse (retry): "+finalResponse);
//		
//			UpdateBonusIdResponse updateResponse = new UpdateBonusIdResponse(finalResponse.getBonusId(), Long.parseLong(request.getExtBonusId()));
//			getCasinoFrbService().updateExternalBonusId(updateResponse);
//			
//		} catch (Exception ex) {
//			log.error("Retry FRB bonus allocation (retry): " + request.toString(), ex);
//			throw ex;
//		}
//
//	}
//
//	public BetResponse handleDuplicateTransactionException(DuplicateTransactionException dte, String userGuid) {
//		BetResponse response = new BetResponse(BetResponse.RESPONSE_SUCCESS, "OK");
//		try {
//			response.setBalanceCents(getCasinoService().handleBalanceRequest(new BalanceRequest(userGuid)).getBalanceCents());
//		} catch (Exception e) {
//			log.warn("Unable to get player balance during duplicate transaction response: " + userGuid);
//		}
//		response.setExtSystemTransactionId(dte.getInternalTransactionId());
//		
//		return response;
//	}
//	
//	private String setRequestParams(Map<String, String> map) throws UnsupportedEncodingException {
//		Iterator<String> iterator = map.keySet().iterator();
//		StringBuilder sb = new StringBuilder();
//		while (iterator.hasNext()) {
//			String key = (String) iterator.next();
//			sb.append(key.trim() + "=");
//			sb.append(map.get(key));
//			if (iterator.hasNext()) {
//				sb.append("&");
//			}
//		}
//		return sb.toString();
//	}

	public boolean isApiCredentialsCorrect(Request request, APIAuthentication apiAuthentication) {
		try {
			if (request.getMethodCall().getAuth().getLogin().contentEquals(apiAuthentication.getBrandConfiguration().getApiLogin()) &&
				request.getMethodCall().getAuth().getPassword().contentEquals(apiAuthentication.getBrandConfiguration().getApiPassword())) {
				return true;
			}
		} catch (Exception ex) {
			log.error("API credentials match failed. request: " + request + " apiAutentication: " + apiAuthentication, ex);
			return false;
		}
		return false;
	}
	
	public lithium.service.casino.provider.sgs.data.response.Response<Result<Extinfo>> createErrorResponse(Request request, ErrorCodes error) {
		
		Result<Extinfo> result = new Result<>(request.getMethodCall().getCall().getSeq(), request.getMethodCall().getCall().getToken());
		result.setErrorCode(error.getCode());
		result.setErrorDescription(error.getDescription());
		result.setExtinfo(new Extinfo());
		
		return new lithium.service.casino.provider.sgs.data.response.Response<Result<Extinfo>>(
				new MethodResponse<Result<Extinfo>>(
						request.getMethodCall().getName(), 
						new Date(), 
						result));
	}
}
