package lithium.service.domain.client;

import lithium.service.Response;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-domain", path="/domain")
public interface ProviderClient {
	@RequestMapping(path="/{domainName}/providers")
	public Response<Iterable<Provider>> list(@PathVariable("domainName") String domainName);
	@RequestMapping(path="/{domainName}/provider/propertiesByProviderId")
	public Response<Iterable<ProviderProperty>> properties(@RequestParam("providerId") Long providerId);
	@RequestMapping(path="/{domainName}/providers/listbydomainandtype")
	public Response<Iterable<Provider>> listByDomainAndType(@PathVariable("domainName") String domainName, @RequestParam("type") String type);
//	@RequestMapping(path="/provider/propertiesByProviderNameAndDomainName")
//	public Response<Iterable<ProviderProperty>> properties(@RequestParam("providerName") String providerName, @RequestParam("domainName") String domainName);

	@Cacheable(value="lithium.service.domain.client.ProviderClient.propertiesByProviderUrlAndDomainName", unless="#result == null")
	@RequestMapping(path="/provider/propertiesByProviderUrlAndDomainName")
	public Response<Iterable<ProviderProperty>> propertiesByProviderUrlAndDomainName(@RequestParam("providerUrl") String providerUrl, @RequestParam("domainName") String domainName);
	@RequestMapping(path="provider/listAllProvidersByType")
	public Response<Iterable<Provider>> listAllProvidersByType(@RequestParam("type") String type);
	@RequestMapping(path="provider/listAllProvidersByTypeAndUrl")
	public Response<Iterable<Provider>> listAllProvidersByTypeAndUrl(@RequestParam("type") String type, @RequestParam("url") String url);


	@Cacheable(value="lithium.service.domain.client.ProviderClient.findByUrlAndDomainName", unless="#result == null")
	@RequestMapping(path="/{domainName}/providers/findByUrlAndDomainName")
	public Response<Provider> findByUrlAndDomainName(@RequestParam("url") String url, @PathVariable("domainName") String domainName);

	@RequestMapping(path = "provider/updateProperty")
	public Response<ProviderProperty> updateProviderProperty(@RequestParam("propertyId") Long propertyId, @RequestParam("value") String value);
}
