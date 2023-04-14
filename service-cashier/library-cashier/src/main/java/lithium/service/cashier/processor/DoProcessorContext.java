package lithium.service.cashier.processor;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lombok.Data;

@Data
public class DoProcessorContext {

	private DoProcessorRequest request;
	private DoProcessorResponse response;

}
