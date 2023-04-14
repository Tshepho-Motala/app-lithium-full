package lithium.service.sms.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;

@RestController
@RequestMapping("/internal/do")
public class DoProviderController {
	@Autowired DoMessage doMessage;
	
	@RequestMapping
	public DoProviderResponse request(@RequestBody DoProviderRequest request) throws Exception {
		return doMessage.run(request);
	}
}