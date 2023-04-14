package lithium.service.limit.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.ExclusionClient;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status489PlayerExclusionNotFoundException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.schemas.exclusion.ExclusionRequest;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionRequest;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionResponse;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.services.ExclusionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/exclusion")
@Slf4j
public class SystemExclusionController implements ExclusionClient {
	@Autowired private ExclusionService service;
	@Autowired private ModelMapper modelMapper;

	private static String SUCCESS = "Success";

	@Override
	@GetMapping("/{domainName}/options/months")
	public List<Integer> optionsInMonths(@PathVariable("domainName") String domainName)
			throws Status550ServiceDomainClientException {
		return service.getExclusionPeriodsInMonths(domainName);
	}

	@Override
	@GetMapping("/lookup")
	public lithium.service.limit.client.objects.PlayerExclusionV2 lookup(
			@RequestParam("playerGuid") String playerGuid) {
		PlayerExclusionV2 playerExclusion = service.lookup(playerGuid);
		lithium.service.limit.client.objects.PlayerExclusionV2 playerExclusionCO =
			new lithium.service.limit.client.objects.PlayerExclusionV2();
		if(playerExclusion != null) {
			modelMapper.map(playerExclusion, playerExclusionCO);
			return playerExclusionCO;
		}
		return null;
	}

	@Override
	@PostMapping("/set")
	public lithium.service.limit.client.objects.PlayerExclusionV2 set(@RequestBody ExclusionRequest request) throws UserNotFoundException,
			Status500InternalServerErrorException, Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			UserClientServiceFactoryException,Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException,
			Status422PlayerRestrictionExclusionException, LithiumServiceClientFactoryException {
		PlayerExclusionV2 playerExclusion = service.set(request.getPlayerGuid(),
			request.getPeriodInMonths(), User.SYSTEM_GUID, request.getExclusionSource(), request.getAdvisor(),
			request.getExclusionEndDate(), null);
		lithium.service.limit.client.objects.PlayerExclusionV2 playerExclusionCO =
			new lithium.service.limit.client.objects.PlayerExclusionV2();
		modelMapper.map(playerExclusion, playerExclusionCO);
		return playerExclusionCO;
	}

	@Override
	@PostMapping("/remove")
	public RemoveExclusionResponse remove(@RequestBody RemoveExclusionRequest request) throws UserNotFoundException, Status500InternalServerErrorException, UserClientServiceFactoryException, LithiumServiceClientFactoryException {
		RemoveExclusionResponse response = RemoveExclusionResponse.builder().build();
		try {
			service.clear(request.getPlayerGuid(), request.getAuthorGuid(), null);
			response.setMessage(SUCCESS);
			return response;
		} catch (Status489PlayerExclusionNotFoundException e) {
			log.warn("Attempting to remove non existing exclusion", e);
			response.setMessage("Failed " + e.getMessage());
		}
		return response;
	}

}
