package lithium.service.cashier.client.internal;

import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient
public interface ProcessorAccountClient {
	@RequestMapping(path="/internal/processor-account/add", method=RequestMethod.POST)
    ProcessorAccountResponse addProcessorAccount(@RequestBody AccountProcessorRequest request) throws Exception;
}
