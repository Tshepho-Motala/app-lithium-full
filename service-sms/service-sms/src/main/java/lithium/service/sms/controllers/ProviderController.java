package lithium.service.sms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.sms.data.entities.Provider;
import lithium.service.sms.services.ProviderService;

@RestController
@RequestMapping("/sms/provider")
public class ProviderController {
	@Autowired ProviderService providerService;
	
	@GetMapping
	private Response<List<Provider>> providers() {
		return Response.<List<Provider>>builder().data(providerService.findAll()).status(Status.OK).build();
	}
}