package lithium.service.mail.controllers.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.mail.client.internal.DoProviderResponse;
import lithium.service.mail.services.provider.DoProvider;

@RestController
@RequestMapping("/internal/callback")
public class DoCallbackController {
	@Autowired DoProvider doProvider;
	
	@RequestMapping
	public void doProviderCallback(@RequestBody DoProviderResponse response) throws Exception {
		doProvider.processProviderCallback(response);
	}
}