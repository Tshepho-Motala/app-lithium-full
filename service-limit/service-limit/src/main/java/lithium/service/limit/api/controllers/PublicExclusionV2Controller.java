package lithium.service.limit.api.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.client.schemas.PublicApiRequest;
import lithium.service.limit.client.schemas.exclusion.PublicApiExclusionRequest;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.services.ExclusionService;
import lithium.service.limit.services.PublicApiAuthenticationService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/exclusion/v2")
@Slf4j
public class PublicExclusionV2Controller {
	@Autowired private ExclusionService service;
	@Autowired private PublicApiAuthenticationService validationService;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired LocaleContextProcessor localeContextProcessor;


	@GetMapping("/lookup")
	public PlayerExclusionV2 lookup(@RequestBody PublicApiRequest request)
		throws  Status500InternalServerErrorException, Status401UnAuthorisedException,
				Status470HashInvalidException, UserNotFoundException, UserClientServiceFactoryException {
		log.debug("PublicExclusionV2Controller.lookup [request="+request+"]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player = userApiInternalClientService.getUserByCellphoneNumber(request.getGroupRef(), request.getMsisdn());

		return service.lookup(player.guid());
	}

	@PostMapping("/set")
	public PlayerExclusionV2 set(@RequestBody PublicApiExclusionRequest request,
								 @RequestParam(value = "locale", required = false) String locale)
			throws UserNotFoundException, Status500InternalServerErrorException, UserClientServiceFactoryException,
			Status470HashInvalidException, Status401UnAuthorisedException,
			Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException, Status409PlayerRestrictionConflictException,
			Status403PlayerRestrictionDeniedException, Status422PlayerRestrictionExclusionException,LithiumServiceClientFactoryException {
		log.debug("PublicExclusionV2Controller.set [request="+request+"]");
		localeContextProcessor.setLocaleContextHolder(locale);
		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player = userApiInternalClientService.getUserByCellphoneNumber(request.getGroupRef(), request.getMsisdn());

		return service.set(player, request.getPeriodInMonths(), request.getApiAuthorizationId(),
			ExclusionSource.EXTERNAL, request.getApiAuthorizationId(), null, null);
	}
}
