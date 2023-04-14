package lithium.service.casino.provider.sgs.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.sgs.config.APIAuthentication;
import lithium.service.casino.provider.sgs.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.sgs.service.SGSService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	@Autowired
	protected SGSService sgsService;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String apiKey, @PathVariable String domainName) {
		
		BrandsConfigurationBrand brandConfiguration = sgsService.getBrandConfiguration(providerUrl, domainName);
		
		log.debug("Retrieved API authentication from url - providerUrl " + providerUrl + " apiKey " + apiKey +
				" brandId " + domainName);
		
		if (brandConfiguration.getCustomerId().isEmpty()) {
			throw new RuntimeException("Brand has no client id configured " + domainName);
		}
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
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