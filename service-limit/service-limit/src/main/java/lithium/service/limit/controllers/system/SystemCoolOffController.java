package lithium.service.limit.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.CoolOffClient;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.schemas.cooloff.CoolOffRequest;
import lithium.service.limit.client.schemas.cooloff.CoolOffResponse;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.services.CoolOffService;
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
@RequestMapping("/system/cooloff")
@Slf4j
public class SystemCoolOffController implements CoolOffClient {
	@Autowired private CoolOffService service;
	@Autowired private ModelMapper modelMapper;

	@Override
	@GetMapping("/{domainName}/options/days")
	public List<Integer> optionsInDays(@PathVariable("domainName") String domainName)
			throws Status550ServiceDomainClientException {
		return service.getCooloffPeriodsInDays(domainName);
	}

	@Override
	@GetMapping("/lookup")
	public lithium.service.limit.client.objects.PlayerCoolOff lookup(@RequestParam("playerGuid") String playerGuid) {
		PlayerCoolOff playerCoolOff = service.lookup(playerGuid);
		lithium.service.limit.client.objects.PlayerCoolOff playerCoolOffCO = new lithium.service.limit.client.objects.PlayerCoolOff();
		modelMapper.map(playerCoolOff, playerCoolOffCO);
		return playerCoolOffCO;
	}

	@Override
	@PostMapping("/set")
	public CoolOffResponse set(@RequestBody CoolOffRequest request)
			throws UserNotFoundException, UserClientServiceFactoryException, LithiumServiceClientFactoryException,
			Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, Status500InternalServerErrorException {
		PlayerCoolOff playerCoolOff = service.set(request.getPlayerGuid(), request.getPeriodInDays(), User.SYSTEM_GUID, null);
		return CoolOffResponse.builder().expiryDate(playerCoolOff.getExpiryDateDisplay()).build();
	}
}
