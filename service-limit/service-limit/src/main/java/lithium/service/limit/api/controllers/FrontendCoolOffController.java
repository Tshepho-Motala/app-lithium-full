package lithium.service.limit.api.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.schemas.cooloff.CoolOffRequest;
import lithium.service.limit.client.schemas.cooloff.CoolOffResponse;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.services.CoolOffService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/cooloff/v1")
public class FrontendCoolOffController {
	@Autowired private CoolOffService service;
	@Autowired private LocaleContextProcessor localeContextProcessor;

	@GetMapping("/options/days")
	public List<Integer> optionsInDays(LithiumTokenUtil tokenUtil) throws Status550ServiceDomainClientException {
		return service.getCooloffPeriodsInDays(tokenUtil.domainName());
	}

	@PostMapping("/set")
	public CoolOffResponse set(@RequestBody CoolOffRequest request, LithiumTokenUtil tokenUtil,
							   @RequestParam(value = "locale", required = false) String locale)
			throws UserNotFoundException, UserClientServiceFactoryException,
			Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, LithiumServiceClientFactoryException,
			Status500InternalServerErrorException {
		localeContextProcessor.setLocaleContextHolder(locale);
		PlayerCoolOff playerCoolOff = service.set(tokenUtil.guid(), request.getPeriodInDays(), tokenUtil.guid(), tokenUtil);
		return CoolOffResponse.builder().expiryDate(playerCoolOff.getExpiryDateDisplay()).build();
	}
}
