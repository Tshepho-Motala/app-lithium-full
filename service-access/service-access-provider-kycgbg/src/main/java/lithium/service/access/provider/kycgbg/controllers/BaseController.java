package lithium.service.access.provider.kycgbg.controllers;

import lithium.service.access.provider.kycgbg.adapter.KycAdapter;
import lithium.service.access.provider.kycgbg.config.APIAuthentication;
import lithium.service.access.provider.kycgbg.config.BrandsConfigurationBrand;
import lithium.service.access.provider.kycgbg.config.KycGbgConfigurationProperties;
import lithium.service.access.provider.kycgbg.services.ApiService;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{domainName}")
public class BaseController {

	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected ApiService kycService;

	@Autowired KycGbgConfigurationProperties properties;

	@Autowired WebServiceTemplate webServiceTemplate;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String domainName) {
		BrandsConfigurationBrand brandConfiguration = kycService.getBrandConfiguration(domainName);
		log.debug("Retrieved API authentication from url - providerName "+providerUrl+" brandId "+domainName);
		
		if (brandConfiguration.getProfileId().isEmpty()) {
			throw new RuntimeException("Brand has no Profile ID configured " + domainName);
		}

		if (brandConfiguration.getUsername().isEmpty()) {
			throw new RuntimeException("Username of account not configured " + domainName);
		}
		
		if (brandConfiguration.getPassword().isEmpty()) {
			throw new RuntimeException("Password of account not configured " + domainName);
		}
		
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Base URL not configured " + domainName);
		}
		return new APIAuthentication(providerUrl, domainName, brandConfiguration);
	}
	
	@ModelAttribute
	protected KycAdapter adapter(@PathVariable String providerUrl, @PathVariable String domainName) {
		APIAuthentication apiAuthentication = getAPIAuthentication(providerUrl, domainName);
		Integer readTimeout = apiAuthentication.getBrandConfiguration().getReadTimeout();
		Integer connectionTimeout = apiAuthentication.getBrandConfiguration().getConnectionTimeout();

		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout((readTimeout!=null)?readTimeout:properties.getReadTimeout())
			.setConnectTimeout((connectionTimeout!=null)?connectionTimeout:properties.getConnectionTimeout())
			.setSocketTimeout((readTimeout!=null)?readTimeout:properties.getReadTimeout())
			.setCookieSpec(CookieSpecs.STANDARD)
			.build();

		CloseableHttpClient httpClient = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(requestConfig)
			.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
			.build();

		HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender(httpClient);

		log.debug("Setting up webServiceTemplate with readTimeout: "+readTimeout+" and connectionTimeout: "+connectionTimeout);

		webServiceTemplate.setMessageSender(httpComponentsMessageSender);
		
		KycAdapter adapter = KycAdapter.builder()
		.webServiceTemplate(webServiceTemplate)
		.build();
		
		return adapter;
	}
}
