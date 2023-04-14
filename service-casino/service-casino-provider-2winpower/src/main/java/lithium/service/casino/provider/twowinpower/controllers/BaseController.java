package lithium.service.casino.provider.twowinpower.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.twowinpower.config.APIAuthentication;
import lithium.service.casino.provider.twowinpower.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.twowinpower.service.TwoWinPowerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	@Autowired
	protected TwoWinPowerService twpService;
	@Autowired
	protected ModelMapper mapper;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(
		@PathVariable String providerUrl,
		@PathVariable String apiKey,
		@PathVariable String domainName
	) {
		BrandsConfigurationBrand bc = twpService.getBrandConfiguration(providerUrl, domainName);
		log.trace("Retrieved API authentication from url - providerUrl " + providerUrl + " apiKey " + apiKey + " brandId " + domainName);
		
		if (bc.getMerchantId().isEmpty()) {
			throw new RuntimeException("Brand has no merchant id configured " + domainName);
		}
		if (bc.getMerchantKey().isEmpty()) {
			throw new RuntimeException("Brand has no merchant key configured " + domainName);
		}
		if (bc.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
		}
		if (bc.getApiKey().isEmpty()) {
			throw new RuntimeException("Brand has no apikey configured " + domainName);
		}
		if (!bc.getApiKey().equals(apiKey)) {
			throw new RuntimeException("Incorrect apikey specified.");
		}
		
		return new APIAuthentication(twpService, apiKey, providerUrl, domainName, bc);
	}
	
	public String getDomainNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[0];
	}
	
	public String getPlayerNameFromPlayerGuid(final String playerGuid) {
		return playerGuid.split("/", 2)[1];
	}
}