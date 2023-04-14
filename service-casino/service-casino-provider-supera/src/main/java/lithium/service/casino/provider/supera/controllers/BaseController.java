package lithium.service.casino.provider.supera.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.provider.supera.config.APIAuthentication;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.service.SuperaService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	@Autowired
	protected SuperaService superaService;
	
	@Autowired protected RestTemplate rest;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String apiKey, @PathVariable String domainName) {
		
		BrandsConfigurationBrand brandConfiguration = superaService.getBrandConfiguration(providerUrl, domainName);
		
		log.debug("Retrieved API authentication from url - providerUrl " + providerUrl + " apiKey " + apiKey +
				" brandId " + domainName);
		
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
		}
		
		if (brandConfiguration.getSaltKey().isEmpty()) {
			throw new RuntimeException("Brand has no salt key configured " + domainName);
		}
		
		if (brandConfiguration.getApiUser().isEmpty()) {
			throw new RuntimeException("Brand has no api user configured " + domainName);
		}
		
		if (brandConfiguration.getApiPassword().isEmpty()) {
			throw new RuntimeException("Brand has no api password configured " + domainName);
		}
		
		return new APIAuthentication(apiKey, providerUrl, domainName, brandConfiguration);
	}
	
	public String getDomainNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[0];
	}

	public String getPlayerNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[1];
	}
}