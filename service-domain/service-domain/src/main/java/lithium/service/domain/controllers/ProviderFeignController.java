package lithium.service.domain.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.DomainProviderLink;
import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/domain/provider")
public class ProviderFeignController extends ProviderController {
	
	@GetMapping("/propertiesByProviderId")
	public Response<Iterable<ProviderProperty>> properties(
		@RequestParam("providerId") Provider provider
		) throws Exception {
		return Response.<Iterable<ProviderProperty>>builder().data(provider.getProperties()).status(Status.OK).build();
	}
	
	@RequestMapping("/propertiesByProviderNameAndDomainName")
	public Response<Iterable<ProviderProperty>> properties(@RequestParam("providerName") String providerName, 
			@RequestParam("domainName") String domainName) {
		Provider p = providerRepository.findByNameAndDomainName(providerName, domainName);
		return Response.<Iterable<ProviderProperty>>builder().data(p.getProperties()).status(Status.OK).build();
	}

	@RequestMapping("/propertiesByProviderUrlAndDomainName")
	public Response<Iterable<ProviderProperty>> propertiesByProviderUrlAndDomainName(@RequestParam("providerUrl") String providerUrl, 
			@RequestParam("domainName") String domainName) {
		DomainProviderLink dpl = domainProviderLinkRepo.findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(domainName, providerUrl);
		Provider p = null;
		if(dpl != null) {
			p = dpl.getProvider();
		}
		//fallback to original in case no link exists
		if(p == null) {
			p = providerRepository.findByUrlAndDomainName(providerUrl, domainName);
		}
		
		if(p == null) {
			log.error("No provider configured for provider url:"+ providerUrl +" and domain: "+ domainName);
			return Response.<Iterable<ProviderProperty>>builder().status(Status.NOT_FOUND).build();
		}
		return Response.<Iterable<ProviderProperty>>builder().data(p.getProperties()).status(Status.OK).build();
		
	}

	@GetMapping("/listAllProvidersByType")
	public Response<Iterable<Provider>> listAllProvidersByType(@RequestParam("type") String type) {
		Iterable<Provider> all = providerRepository.findByProviderTypeNameOrderByPriority(type);
		return Response.<Iterable<Provider>>builder().data(all).build();
	}
	
	@RequestMapping(path="/listAllProvidersByTypeAndUrl")
	public Response<Iterable<Provider>> listAllProvidersByTypeAndUrl(@RequestParam("type") String type, @RequestParam("url") String url) {
		Iterable<Provider> all = providerRepository.findByProviderTypeNameAndUrlOrderByPriority(type, url);
		return Response.<Iterable<Provider>>builder().data(all).build();
	}

  @RequestMapping(path="/updateProperty")
  public Response<ProviderProperty> updateProviderProperty(@RequestParam("propertyId") Long propertyId, @RequestParam("value") String value) {
    ProviderProperty property = providerPropertyRepository.findOne(propertyId);
    property.setValue(value);
    property = providerPropertyRepository.save(property);
    return Response.<ProviderProperty>builder().data(property).build();
  }
}
