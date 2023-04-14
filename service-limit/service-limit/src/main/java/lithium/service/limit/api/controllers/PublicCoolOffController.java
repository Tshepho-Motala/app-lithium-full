package lithium.service.limit.api.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.schemas.PublicApiRequest;
import lithium.service.limit.client.schemas.cooloff.CoolOffResponse;
import lithium.service.limit.client.schemas.cooloff.PublicApiCoolOffRequest;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.services.CoolOffService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/cooloff/v1")
@Slf4j
public class PublicCoolOffController {
	@Autowired private CoolOffService service;
	@Autowired private PublicApiAuthenticationService validationService;
	@Autowired private UserApiInternalClientService userApiInternalClientService;

	@GetMapping("/lookup")
	public PlayerCoolOff lookup(@RequestBody PublicApiRequest request)
			throws  Status500InternalServerErrorException, Status401UnAuthorisedException,
					Status470HashInvalidException, UserNotFoundException, UserClientServiceFactoryException {
		log.debug("PublicCoolOffController.lookup [request="+request+"]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player = userApiInternalClientService.getUserByCellphoneNumber(request.getGroupRef(), request.getMsisdn());

		return service.lookup(player.guid());
	}

	@PostMapping("/set")
	public CoolOffResponse set(@RequestBody PublicApiCoolOffRequest request)
			throws UserNotFoundException, UserClientServiceFactoryException,
			Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, Status470HashInvalidException, Status401UnAuthorisedException,
			LithiumServiceClientFactoryException, Status500InternalServerErrorException {
		log.debug("PublicCoolOffController.set [request="+request+"]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player = userApiInternalClientService.getUserByCellphoneNumber(request.getGroupRef(), request.getMsisdn());
		PlayerCoolOff playerCoolOff = service.set(player, request.getPeriodInDays(), request.getApiAuthorizationId(), null);

		return CoolOffResponse.builder().expiryDate(playerCoolOff.getExpiryDateDisplay()).build();
	}
}
