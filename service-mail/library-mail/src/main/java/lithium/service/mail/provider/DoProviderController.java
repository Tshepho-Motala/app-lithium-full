package lithium.service.mail.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;

@RestController
@RequestMapping("/internal/do")
public class DoProviderController {
	@Autowired
	@Lazy
	DoMessage doMessage;
	
	@RequestMapping
	public DoProviderResponse request(@RequestBody DoProviderRequest request) throws Exception {
		return doMessage.run(request);
	}
}