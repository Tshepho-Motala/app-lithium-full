package lithium.service.limit.api.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.schemas.cooloff.CoolOffRequest;
import lithium.service.limit.client.schemas.cooloff.CoolOffResponse;
import lithium.service.limit.client.schemas.exclusion.ExclusionRequest;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.services.ExclusionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/frontend/exclusion/v2")
public class FrontendExclusionV2Controller {
	@Autowired private ExclusionService service;
	@Autowired LocaleContextProcessor localeContextProcessor;

	@GetMapping("/options/months")
	public List<Integer> optionsInMonths(LithiumTokenUtil tokenUtil) throws Status550ServiceDomainClientException {
		return service.getExclusionPeriodsInMonths(tokenUtil.domainName());
	}

	@PostMapping("/set")
	public PlayerExclusionV2 set(
			@RequestBody ExclusionRequest request,
			LithiumTokenUtil tokenUtil,
			@RequestParam(value = "locale", required = false) String locale
	) throws
			UserNotFoundException,
			Status500InternalServerErrorException,
			UserClientServiceFactoryException,
			Status491PermanentSelfExclusionException,
			Status490SoftSelfExclusionException,
			Status403PlayerRestrictionDeniedException,
			Status409PlayerRestrictionConflictException,
			Status422PlayerRestrictionExclusionException,
			LithiumServiceClientFactoryException {
			localeContextProcessor.setLocaleContextHolder(locale);
			return service.set(tokenUtil.guid(), request.getPeriodInMonths(), tokenUtil.guid(),
			null, null, null, tokenUtil);
	}
}
