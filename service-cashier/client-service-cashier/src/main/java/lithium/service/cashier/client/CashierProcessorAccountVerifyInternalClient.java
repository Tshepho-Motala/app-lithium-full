package lithium.service.cashier.client;

import lithium.service.Response;
import lithium.service.cashier.client.internal.VerifyProcessorAccountRequest;
import lithium.service.cashier.client.internal.VerifyProcessorAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="service-cashier")
public interface CashierProcessorAccountVerifyInternalClient {
	@RequestMapping(path="/internal/processor-account/verify", method=RequestMethod.POST)
	public Response<VerifyProcessorAccountResponse> verifyAccount(@RequestBody VerifyProcessorAccountRequest request);
}
