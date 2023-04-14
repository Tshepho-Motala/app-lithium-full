package lithium.service.limit.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.objects.PlayerExclusionV2;
import lithium.service.limit.client.schemas.exclusion.ExclusionRequest;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionRequest;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionResponse;
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
public interface ExclusionClient {
	@RequestMapping(method=RequestMethod.GET, path="/system/exclusion/{domainName}/options/months")
	public List<Integer> optionsInMonths(@PathVariable("domainName") String domainName)
		throws Status550ServiceDomainClientException;

	@RequestMapping(method=RequestMethod.GET, path="/system/exclusion/lookup")
	public PlayerExclusionV2 lookup(@RequestParam("playerGuid") String playerGuid);

	@RequestMapping(method=RequestMethod.POST, path="/system/exclusion/set")
	public PlayerExclusionV2 set(@RequestBody ExclusionRequest request)
			throws UserNotFoundException, Status500InternalServerErrorException, Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException, UserClientServiceFactoryException, Status403PlayerRestrictionDeniedException,
			Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException, LithiumServiceClientFactoryException;

	@RequestMapping(method=RequestMethod.POST, path="/system/exclusion/remove")
	public RemoveExclusionResponse remove(@RequestBody RemoveExclusionRequest request)
			throws UserNotFoundException, Status500InternalServerErrorException, UserClientServiceFactoryException, LithiumServiceClientFactoryException;

}
