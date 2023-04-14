package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.client.schemas.exclusion.ExclusionRequest;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.services.ExclusionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/backoffice/exclusion/{domainName}")
@Slf4j
public class BackofficeExclusionController {
	@Autowired private ExclusionService service;

	@GetMapping("/options/months")
	public Response<List<Integer>> optionsInMonths(@PathVariable("domainName") String domainName) {
		try {
			return Response.<List<Integer>>builder().data(service.getExclusionPeriodsInMonths(domainName))
				.status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to get exclusion period options in months [domainName="+domainName+"] "
				+ e.getMessage(), e);
			return Response.<List<Integer>>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@GetMapping("/lookup")
	public Response<PlayerExclusionV2> lookup(@RequestParam("playerGuid") String playerGuid) {
		return Response.<PlayerExclusionV2>builder().data(service.lookup(playerGuid))
			.status(Response.Status.OK).build();
	}

	@PostMapping("/set")
	public Response<PlayerExclusionV2> set(@RequestBody ExclusionRequest request, LithiumTokenUtil tokenUtil) {
		try {
			PlayerExclusionV2 playerExclusionV2 = service.set(request.getPlayerGuid(), request.getPeriodInMonths(), tokenUtil.guid(), null, null,
							null, tokenUtil);
			if(playerExclusionV2 == null) {
				return Response.<PlayerExclusionV2>builder()
						.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			} else {
				return Response.<PlayerExclusionV2>builder()
						.data(playerExclusionV2)
						.status(Response.Status.OK).build();
			}

		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("Failed to set player exclusion [request="+request+"] " + e.getMessage(), e);
			return Response.<PlayerExclusionV2>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/clear")
	public Response<Boolean> clear(@RequestParam("playerGuid") String playerGuid, LithiumTokenUtil tokenUtil) {
		try {
			service.clear(playerGuid, tokenUtil.guid(), tokenUtil);
			return Response.<Boolean>builder().data(true).status(Response.Status.OK).build();
		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("Failed to clear player exclusion [playerGuid="+playerGuid+"] "
				+ e.getMessage(), e);
			return Response.<Boolean>builder().data(false).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}
}
