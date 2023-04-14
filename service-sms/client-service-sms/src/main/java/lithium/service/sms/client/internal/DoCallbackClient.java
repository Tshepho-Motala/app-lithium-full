package lithium.service.sms.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-sms")
public interface DoCallbackClient {
	@RequestMapping(path="/internal/callback", method=RequestMethod.POST) 
	public void doProviderCallback(@RequestBody DoProviderResponse response);
}