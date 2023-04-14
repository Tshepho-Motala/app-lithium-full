package lithium.service.limit.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.objects.PlayerCoolOff;
import lithium.service.limit.client.schemas.cooloff.CoolOffRequest;
import lithium.service.limit.client.schemas.cooloff.CoolOffResponse;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-limit")
public interface CoolOffClient {
	@RequestMapping(method=RequestMethod.GET, path="/system/cooloff/{domainName}/options/days")
	public List<Integer> optionsInDays(@PathVariable("domainName") String domainName)
		throws Status550ServiceDomainClientException;

	@RequestMapping(method=RequestMethod.GET, path="/system/cooloff/lookup")
	public PlayerCoolOff lookup(@RequestParam("playerGuid") String playerGuid);

	@RequestMapping(method=RequestMethod.POST, path="/system/cooloff/set")
	public CoolOffResponse set(@RequestBody CoolOffRequest request)
			throws UserNotFoundException, UserClientServiceFactoryException, LithiumServiceClientFactoryException,
			Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
			Status496PlayerCoolingOffException, Status500InternalServerErrorException;
}
