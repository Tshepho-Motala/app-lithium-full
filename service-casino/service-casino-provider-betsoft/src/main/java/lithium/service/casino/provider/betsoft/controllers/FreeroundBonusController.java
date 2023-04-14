package lithium.service.casino.provider.betsoft.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.betsoft.BetsoftModuleInfo;
import lithium.service.casino.provider.betsoft.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.betsoft.data.request.AwardBonusRequest;
import lithium.service.casino.provider.betsoft.data.request.CancelBonusRequest;
import lithium.service.casino.provider.betsoft.data.request.CheckBonusRequest;
import lithium.service.casino.provider.betsoft.data.request.GetBonusInfoRequest;
import lithium.service.casino.provider.betsoft.data.requestresponse.AwardBonusRequestResponse;
import lithium.service.casino.provider.betsoft.data.requestresponse.CancelBonusRequestResponse;
import lithium.service.casino.provider.betsoft.data.requestresponse.CheckBonusRequestResponse;
import lithium.service.casino.provider.betsoft.data.requestresponse.GetBonusInfoRequestResponse;
import lithium.service.casino.provider.betsoft.data.response.AwardBonusResponse;
import lithium.service.casino.provider.betsoft.data.response.CancelBonusResponse;
import lithium.service.casino.provider.betsoft.data.response.CheckBonusResponse;
import lithium.service.casino.provider.betsoft.data.response.GetBonusInfoResponse;
import lithium.service.casino.provider.betsoft.service.BetsoftService;
import lithium.service.mail.client.objects.SystemEmailData;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value="/casino/frb")
public class FreeroundBonusController {
	private static final String FRB_AWARD_BONUS_URL_EXT = "/frbaward.do?";
	private static final String FRB_CHECK_BONUS_URL_EXT = "/frbcheck.do?";
	private static final String FRB_CANCEL_BONUS_URL_EXT = "/frbcancel.do?";
	private static final String FRB_GET_BONUS_INFO_URL_EXT = "/frbinfo.do?";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	protected BetsoftModuleInfo moduleInfo;
	
	@Autowired
	private BetsoftService betsoftService;
	
	@Autowired
	private ModelMapper mapper;
	
	private RestTemplate getRestTemplate() {
//		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
//		for (HttpMessageConverter<?> converter : converters) {
//			if (converter instanceof MappingJackson2HttpMessageConverter) {
//				try {
//					((MappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML));
//					((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(Charset.forName("ISO-8859-1"));
//				} catch (Exception e) {
//					log.error(e.getMessage(), e);
//				}
//			}
//		}
		restTemplate.setInterceptors(Arrays.asList(new BetsoftXmlInterceptor()));
		return restTemplate;
	}
	
	/**
	 * Sets the freeround bonus for a customer on the specified games
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws RestClientException
	 */
	@RequestMapping(value = "/awardbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.AwardBonusResponse awardBonus(
		@RequestBody lithium.service.casino.client.objects.request.AwardBonusRequest inRequest
	) throws Exception {
		log.debug("AwardBonusRequest1 : "+inRequest);
		AwardBonusRequest request = mapper.map(inRequest, AwardBonusRequest.class);
		BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), inRequest.getDomainName());
		
		request.setBankId(bc.getBankId());
		request.setHash(request.calculateHash(bc.getHashPassword()));
		log.debug("AwardBonusRequest2 : "+request);
		AwardBonusRequestResponse response = getRestTemplate().getForObject(
			bc.getBaseUrl()+FRB_AWARD_BONUS_URL_EXT+setRequestParams(request.getParamMap()),
			AwardBonusRequestResponse.class,
			request.getParamMap()
		);
		log.debug("AwardBonusRequestResponse: "+response);
		if (!(response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_SUCCESS) || response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_CODE_BONUS_CODE_ALREADY_EXISTS))) {
			log.error("AwardBonusRequestResponse: "+response);
			try {
				betsoftService.getMailService().save(SystemEmailData.builder()
				.domainName(inRequest.getDomainName())
				.priority(1)
				.subject("External bonus allocation failure for " + inRequest.getUserId())
				.body("External bonus allocation attempt failed.\r\n" + response.toString())
				.userGuid(inRequest.getUserId())
				.build());
			} catch (Exception ex) {
				log.error("Unable to send system mail to mail service. " + ex.getMessage(), ex);
			}
			betsoftService.retryFrbAllocation(request, bc, getRestTemplate(), FRB_AWARD_BONUS_URL_EXT, inRequest.getDomainName());
		}
		
		if (response.getResponse().getResult().equalsIgnoreCase(AwardBonusResponse.RESPONSE_CODE_BONUS_CODE_ALREADY_EXISTS)) {
			log.warn("AwardBonusRequestResponse:  "+response);
			response.getResponse().setCode(AwardBonusResponse.RESPONSE_SUCCESS);
		}
		
		lithium.service.casino.client.objects.response.AwardBonusResponse finalResponse = mapper.map(response.getResponse(), lithium.service.casino.client.objects.response.AwardBonusResponse.class);
		log.debug("AwardBonusResponse: "+finalResponse);
		return finalResponse;
	}
	
	/**
	 * In case of network failure while awarding freeround bonus, the status of
	 * the bonus can be checked
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws RestClientException
	 */
	@RequestMapping(value = "/checkbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.CheckBonusResponse checkBonus(
		@RequestBody lithium.service.casino.client.objects.request.CheckBonusRequest inRequest
	) throws Exception {
		CheckBonusRequest request = mapper.map(inRequest, CheckBonusRequest.class);
		BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), inRequest.getDomainName());

		request.setBankId(bc.getBankId());
		request.setHash(request.calculateHash(bc.getHashPassword()));
		
		log.info("FreeroundBetController " + request);
		
		CheckBonusRequestResponse response = getRestTemplate().getForObject(bc.getBaseUrl() 
				+ FRB_CHECK_BONUS_URL_EXT + setRequestParams(request.getParamMap()), CheckBonusRequestResponse.class, request.getParamMap());
		
		if (!response.getResponse().getResult().equalsIgnoreCase(CheckBonusResponse.RESPONSE_SUCCESS)) {
			log.error("An error occurred (" + response.getResponse().getCode() + " - " + response.getResponse().getDescription() + ")");
		}
		
		return mapper.map(response, lithium.service.casino.client.objects.response.CheckBonusResponse.class);
	}
	
	/**
	 * Cancel a freeround bonus
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws RestClientException 
	 */
	@RequestMapping(value = "/cancelbonus")
	public @ResponseBody lithium.service.casino.client.objects.response.CancelBonusResponse cancelBonus(
		@RequestBody lithium.service.casino.client.objects.request.CancelBonusRequest inRequest
	) throws Exception {
		CancelBonusRequest request = mapper.map(inRequest, CancelBonusRequest.class);
		BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), inRequest.getDomainName());

		request.setHash(request.calculateHash(bc.getHashPassword()));
		
		log.info("FreeroundBetController " + request);
		
		CancelBonusRequestResponse response = getRestTemplate().getForObject(bc.getBaseUrl() 
				+ FRB_CANCEL_BONUS_URL_EXT + setRequestParams(request.getParamMap()), CancelBonusRequestResponse.class, request.getParamMap());
		
		if (!response.getResponse().getResult().equalsIgnoreCase(CancelBonusResponse.RESPONSE_SUCCESS)) {
			log.error("An error occurred (" + response.getResponse().getCode() + " - " + response.getResponse().getDescription() + ")");
		}
		
		return mapper.map(response, lithium.service.casino.client.objects.response.CancelBonusResponse.class);
	}
	
	/**
	 * Gets bonus info for a customer
	 * 
	 * @param request
	 * @param apiAuthentication
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws RestClientException 
	 */
	@RequestMapping(value = "/getbonusinfo")
	public @ResponseBody lithium.service.casino.client.objects.response.GetBonusInfoResponse getBonusInfo(
		@RequestBody lithium.service.casino.client.objects.request.GetBonusInfoRequest inRequest
	) throws Exception {
		GetBonusInfoRequest request = mapper.map(inRequest, GetBonusInfoRequest.class);
		BrandsConfigurationBrand bc = betsoftService.getBrandConfiguration(moduleInfo.getModuleName(), inRequest.getDomainName());

		request.setBankId(bc.getBankId());
		request.setHash(request.calculateHash(bc.getHashPassword()));
		
		log.info("GetBonusInfoRequest: "+request);
		GetBonusInfoRequestResponse response = getRestTemplate().getForObject(
			bc.getBaseUrl()+FRB_GET_BONUS_INFO_URL_EXT+setRequestParams(request.getParamMap()),
			GetBonusInfoRequestResponse.class,
			request.getParamMap()
		);
		log.info("GetBonusInfoRequestResponse: "+response);
		
		if (!response.getResponse().getResult().equalsIgnoreCase(GetBonusInfoResponse.RESPONSE_SUCCESS)) {
			log.error("An error occurred GetBonusInfoRequestResponse: "+response);
		}
		
		return mapper.map(response.getResponse(), lithium.service.casino.client.objects.response.GetBonusInfoResponse.class);
	}
	
	private String setRequestParams(Map<String, String> map) throws UnsupportedEncodingException {
		Iterator<String> iterator = map.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			sb.append(key.trim() + "=");
			sb.append(map.get(key));
			if (iterator.hasNext()) {
				sb.append("&");
			}
		}
		return sb.toString();
	}
	
	public class BetsoftXmlInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			ClientHttpResponse response = execution.execute(request, body);
			HttpHeaders headers = response.getHeaders();
			if (headers.containsKey("Content-Type")) {
				headers.remove("Content-Type");
			}
			headers.add("Content-Type", "application/xml");
			return response;
		}
	}
}