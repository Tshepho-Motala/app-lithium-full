package lithium.service.cashier.processor;

import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;

public interface DoProcessorInterface {

	public DoProcessorResponseStatus deposit(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception;
	public DoProcessorResponseStatus withdraw(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception;
	
}
