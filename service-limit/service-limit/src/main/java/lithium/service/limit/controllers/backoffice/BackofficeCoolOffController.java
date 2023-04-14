package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.client.schemas.cooloff.CoolOffRequest;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.services.CoolOffService;
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

@Slf4j
@RestController
@RequestMapping("/backoffice/cooloff/{domainName}")
public class BackofficeCoolOffController {
	@Autowired private CoolOffService service;

	@GetMapping("/options/days")
	public Response<List<Integer>> optionsInDays(@PathVariable("domainName") String domainName) {
		try {
			return Response.<List<Integer>>builder().data(service.getCooloffPeriodsInDays(domainName))
				.status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to get cool off period options in days [domainName="+domainName+"] " + e.getMessage(), e);
			return Response.<List<Integer>>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@GetMapping("/lookup")
	public Response<PlayerCoolOff> lookup(@RequestParam("playerGuid") String playerGuid) {
		return Response.<PlayerCoolOff>builder().data(service.lookup(playerGuid)).status(Response.Status.OK).build();
	}

	@PostMapping("/set")
	public Response<PlayerCoolOff> set(@RequestBody CoolOffRequest request, LithiumTokenUtil tokenUtil) {
		try {
			return Response.<PlayerCoolOff>builder()
				.data(service.set(request.getPlayerGuid(), request.getPeriodInDays(), tokenUtil.guid(), tokenUtil))
				.status(Response.Status.OK).build();
		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("Failed to set player cool off [request="+request+"] " + e.getMessage(), e);
			return Response.<PlayerCoolOff>builder().data(null).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}

	@PostMapping("/clear")
	public Response<Boolean> clear(@RequestParam("playerGuid") String playerGuid, LithiumTokenUtil tokenUtil) {
		try {
			service.clear(playerGuid, tokenUtil.guid(), tokenUtil);
			return Response.<Boolean>builder().data(true).status(Response.Status.OK).build();
		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("Failed to clear player cool off [playerGuid="+playerGuid+"] "
				+ e.getMessage(), e);
			return Response.<Boolean>builder().data(false).status(Response.Status.INTERNAL_SERVER_ERROR)
				.message(e.getMessage()).build();
		}
	}
}
