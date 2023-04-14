package lithium.service.casino.api.frontend.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.api.frontend.schema.SessionRecap;
import lithium.service.casino.service.SessionRecapService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/session-recap")
public class SessionRecapController {
	@Autowired private SessionRecapService service;

	@GetMapping
	public SessionRecap sessionRecap(LithiumTokenUtil tokenUtil) throws Status550ServiceDomainClientException,
			Status500InternalServerErrorException {
		return service.sessionRecap(tokenUtil);
	}
}
