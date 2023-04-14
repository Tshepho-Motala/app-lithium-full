package lithium.service.casino.provider.rival.controllers;

import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.provider.rival.config.APIAuthentication;
import lithium.service.casino.provider.rival.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.rival.service.RivalService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/{providerUrl}/{apiKey}/{domainName}")
public class BaseController {
	
	private static final Log log = LogFactory.getLog(BaseController.class);
	public static final String PROVIDER_SERVICE_NAME = "rival";
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper;

	@Autowired RivalService rivalService;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String apiKey, @PathVariable String domainName) {

		BrandsConfigurationBrand brandConfiguration = rivalService.getBrandConfiguration(providerUrl, domainName);
		
		log.debug("Retrieved API authentication from url - providerName " + providerUrl + " apiKey " + apiKey +
				" brandId " + domainName);
		
		if (brandConfiguration.getHashPassword().isEmpty()) {
			throw new RuntimeException("Brand has no secure hash configured " + domainName);
		}
		if (brandConfiguration.getBaseUrl().isEmpty()) {
			throw new RuntimeException("Brand has no base url configured " + domainName);
		}
		
		return new APIAuthentication(apiKey, providerUrl, domainName, brandConfiguration);
	}
		
	CasinoClient getCasinoService() {
		CasinoClient cl = null;
		try {
			cl = services.target(CasinoClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino service", e);
		}
		
		return cl;
	}
}