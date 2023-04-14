package lithium.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.client.provider.ProviderConfig;


@FeignClient(name="lithium-service")
public interface LithiumServiceClient {
	
	@RequestMapping(path="/modules/providersByTypeString", method=RequestMethod.GET)
	public List<ProviderConfig> getProvidersByTypeString(@RequestParam(name="providerTypeString") String providerTypeString);
//	@RequestMapping(path="/providersByType")
//	public List<ProviderConfig> getProvidersByType(@RequestParam(name="providerType") ProviderType providerType);
	
	
}
