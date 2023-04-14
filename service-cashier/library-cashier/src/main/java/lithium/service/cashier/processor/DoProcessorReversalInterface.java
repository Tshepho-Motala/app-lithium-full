package lithium.service.cashier.processor;

import lithium.service.cashier.client.frontend.DoErrorException;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import org.springframework.web.client.RestTemplate;

public interface DoProcessorReversalInterface {
	public DoProcessorResponseStatus reverse(DoProcessorRequest request, DoProcessorResponse response, DoProcessorContext context, RestTemplate rest) throws DoErrorException, Exception;

}
