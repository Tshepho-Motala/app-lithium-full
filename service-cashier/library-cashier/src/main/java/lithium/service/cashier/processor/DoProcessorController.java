package lithium.service.cashier.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;

@RestController
@RequestMapping("/internal/do")
public class DoProcessorController {
	@Autowired WebApplicationContext beanContext;

	@RequestMapping
	public DoProcessorResponse request(@RequestBody DoProcessorRequest request) {
		DoProcessorMachine machine = beanContext.getBean(DoProcessorMachine.class);
		return machine.run(request);
	}
}
