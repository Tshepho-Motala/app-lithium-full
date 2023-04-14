package lithium.service.cashier.processor.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.client.external.DoProcessorCallbackResponse;

@RestController
@RequestMapping("/internal/callback/do")
public class DoProcessorCallbackController {
	@Autowired
	private WebApplicationContext beanContext;
	
	@RequestMapping
	public DoProcessorCallbackResponse doCallback(@RequestBody DoProcessorCallbackRequest request) {
		DoProcessorCallbackMachine machine = beanContext.getBean(DoProcessorCallbackMachine.class);
		return machine.run(request);
	}
}