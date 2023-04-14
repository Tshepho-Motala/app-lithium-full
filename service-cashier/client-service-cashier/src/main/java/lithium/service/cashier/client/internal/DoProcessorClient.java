package lithium.service.cashier.client.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-cashier-processor")
public interface DoProcessorClient {
	@RequestMapping(path="/internal/do", method=RequestMethod.POST) 
	public DoProcessorResponse doPost(@RequestBody DoProcessorRequest request);
}