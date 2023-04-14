package lithium.service.access.provider.gamstop.controllers;

import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.provider.gamstop.adapter.GamstopAdapter;
import lithium.service.access.provider.gamstop.config.APIAuthentication;
import lithium.service.access.provider.gamstop.config.BrandsConfigurationBrand;
import lithium.service.access.provider.gamstop.services.ApiService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{domainName}")
public class BaseController {
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected ApiService apiService;

	@Autowired
	@Qualifier("lithium.rest")
	private RestTemplateBuilder restTemplateBuilder;
	
	@ModelAttribute
    APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String domainName) throws Status513InvalidDomainConfigurationException, Status550ServiceDomainClientException {
		BrandsConfigurationBrand brandConfiguration = apiService.getBrandConfiguration(providerUrl, domainName);
		log.debug("Retrieved API authentication from url - providerName "+providerUrl+" brandId "+domainName);

		if (StringUtils.isBlank(brandConfiguration.getApiKey())) {
			throw new Status513InvalidDomainConfigurationException("API Key not configured for " + domainName);
		}
		
		if (StringUtils.isBlank(brandConfiguration.getPlatformUrl())) {
			throw new Status513InvalidDomainConfigurationException("URL not configured for " + domainName);
		}

		if (StringUtils.isBlank(brandConfiguration.getBatchPlatformUrl())) {
			throw new Status513InvalidDomainConfigurationException("Batch URL not configured for " + domainName);
		}
		return new APIAuthentication(providerUrl, domainName, brandConfiguration);
	}
	
	@ModelAttribute
	protected GamstopAdapter adapter() {
		RestTemplate restTemplate = restTemplateBuilder.build();
		GamstopAdapter adapter = GamstopAdapter.builder()
		.restTemplate(restTemplate)
		.build();
		return adapter;
	}
}
