package lithium.service.mail.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.mail.data.entities.Provider;
import lithium.service.mail.services.ProviderService;

@RestController
@RequestMapping("/mail/provider")
public class ProviderController {
	@Autowired ProviderService providerService;
	
	@GetMapping
	private Response<List<Provider>> providers() {
		return Response.<List<Provider>>builder().data(providerService.findAll()).status(Status.OK).build();
	}
}