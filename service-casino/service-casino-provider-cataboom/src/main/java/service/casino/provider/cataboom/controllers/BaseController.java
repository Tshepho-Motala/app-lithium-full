package service.casino.provider.cataboom.controllers;

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
import service.casino.provider.cataboom.CataboomModuleInfo.ConfigProperties;
import service.casino.provider.cataboom.config.APIAuthentication;
import service.casino.provider.cataboom.config.BrandsConfigurationBrand;
import service.casino.provider.cataboom.services.CataboomService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;



@RestController
@RequestMapping("/{providerUrl}/{domainName}")
public class BaseController {
	
	private static final Log log = LogFactory.getLog(BaseController.class);
	public static final String PROVIDER_SERVICE_NAME = "cataboom";
	
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	@Autowired
	protected CataboomService cataboomService;
	
	@ModelAttribute
	APIAuthentication getAPIAuthentication(@PathVariable String providerUrl, @PathVariable String domainName) {
		
		BrandsConfigurationBrand brandConfiguration = cataboomService.getBrandConfiguration(providerUrl, domainName);
		log.debug("Retrieved API authentication from url - providerName " + providerUrl +
				" brandId " + domainName);
		
		if (brandConfiguration.getBaseurl().isEmpty()) {
			throw new RuntimeException("Brand has no baseUrl configured " + domainName);
		}
		
		

		return new APIAuthentication( providerUrl, domainName, brandConfiguration);
	}
}