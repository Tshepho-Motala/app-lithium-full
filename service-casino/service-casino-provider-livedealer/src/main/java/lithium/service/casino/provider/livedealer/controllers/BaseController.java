package lithium.service.casino.provider.livedealer.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.provider.livedealer.LivedealerModuleInfo.ConfigProperties;
import lithium.service.casino.provider.livedealer.config.APIAuthentication;
import lithium.service.casino.provider.livedealer.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.livedealer.service.LivedealerService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.games.client.GamesClient;


@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	
	private static final Log log = LogFactory.getLog(BaseController.class);
	public static final String PROVIDER_SERVICE_NAME = "livedealer";
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected LivedealerService livedealerService;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String apiKey, @PathVariable String domainName) {
		
		BrandsConfigurationBrand brandConfiguration = livedealerService.getBrandConfiguration(providerUrl, domainName);
		log.debug("Retrieved API authentication from url - providerName " + providerUrl + " apiKey " + apiKey +
				" brandId " + domainName);
		
		if (brandConfiguration.getClientUser().isEmpty()) {
			throw new RuntimeException("Brand has no client password configured " + domainName);
		}
		
		if (brandConfiguration.getClientPassword().isEmpty()) {
			throw new RuntimeException("Brand has no client password configured " + domainName);
		}
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
		}

		return new APIAuthentication(apiKey, providerUrl, domainName, brandConfiguration);
	}
}