package lithium.service.cashier.client.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-cashier-processor-callback")
public interface DoProcessorCallbackClient {
	@RequestMapping(path="/internal/callback/do", method=RequestMethod.POST) 
	public DoProcessorCallbackResponse doCallback(@RequestBody DoProcessorCallbackRequest request);
}