package lithium.service.mail.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-mail-provider")
public interface DoProviderClient {
	@RequestMapping(path="/internal/do", method=RequestMethod.POST) 
	public DoProviderResponse doPost( @RequestBody DoProviderRequest request);
}