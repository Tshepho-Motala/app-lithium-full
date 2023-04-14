package lithium.service.casino.provider.betsoft.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.provider.betsoft.config.APIAuthentication;
import lithium.service.casino.provider.betsoft.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.betsoft.service.BetsoftService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	@Autowired
	protected BetsoftService betsoftService;
	
	@Autowired
	protected ModelMapper mapper;

	@Value("${lithium.service.casino.provider.betsoft.disable-hash:false}")
	protected boolean disableHash;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String apiKey, @PathVariable String domainName) {
		
		BrandsConfigurationBrand brandConfiguration = betsoftService.getBrandConfiguration(providerUrl, domainName);
		
		log.debug("Retrieved API authentication from url - providerUrl " + providerUrl + " apiKey " + apiKey +
				" brandId " + domainName);
		
		if (brandConfiguration.getHashPassword().isEmpty()) {
			throw new RuntimeException("Brand has no secure hash configured " + domainName);
		}
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
		}
		if (brandConfiguration.getBankId().isEmpty()) {
			throw new RuntimeException("Brand has no bank id configured " + domainName);
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