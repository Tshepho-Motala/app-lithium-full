package lithium.service.limit.api.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status404RestrictionNotFoundException;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.schemas.dwh.DwhRestrictionRequest;
import lithium.service.limit.client.schemas.dwh.DwhRestrictionResponse;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.services.PublicApiAuthenticationService;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/restriction/v1")
@Slf4j
public class ExternalDwhRestrictionController {
	@Autowired RestrictionService restrictionService;
	@Autowired UserRestrictionService userRestrictionService;
	@Autowired PublicApiAuthenticationService validationService;
	@Autowired UserApiInternalClientService userApiInternalClientService;

	@PostMapping("/get")
	public List<DwhRestrictionResponse> get(@RequestBody DwhRestrictionRequest request)
			throws Status401UnAuthorisedException, Status470HashInvalidException, Status500InternalServerErrorException {
		log.debug("ExternalDwhRestrictionController.get [request="+request+"]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		List<DomainRestrictionSet> dwhRestrictions = restrictionService.findByDomainNameAndEnabledTrueAndDwhVisibleTrue(request.getUserGuid().split("/")[0]);

		return getDwhRestrictionResponseList(dwhRestrictions);
	}

	@PostMapping("/lookup")
	public List<DwhRestrictionResponse> lookup(@RequestBody DwhRestrictionRequest request)
		throws  Status401UnAuthorisedException, Status404UserNotFoundException,
			Status470HashInvalidException, Status500InternalServerErrorException {
		log.debug("ExternalDwhRestrictionController.lookup [request=" + request + "]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player;
		try {
			player = userApiInternalClientService.getUserByGuid(request.getUserGuid());
		} catch (UserNotFoundException | UserClientServiceFactoryException userNotFound) {
			throw new Status404UserNotFoundException("Player not found");
		}

		List<DomainRestrictionSet> dwhVisibleTrue = restrictionService.findByDomainNameAndEnabledTrueAndDwhVisibleTrue(request.getUserGuid().split("/")[0]);

		return userRestrictionService.getActiveUserRestrictions(player.guid(), null)
				.stream().filter(userRestrictionSet -> dwhVisibleTrue.stream()
						.anyMatch(domainRestrictionSet -> userRestrictionSet.getSet().getId().equals(domainRestrictionSet.getId())))
				.map(userRestrictionSet -> getDwhRestrictionResponse(userRestrictionSet.getSet()))
				.collect(Collectors.toList());
	}

	@PostMapping("/set")
	public Response<DwhRestrictionResponse> set(@RequestBody DwhRestrictionRequest request)
			throws Status401UnAuthorisedException, Status404RestrictionNotFoundException,
			Status404UserNotFoundException, Status470HashInvalidException, Status500InternalServerErrorException, Status422PlayerRestrictionExclusionException {
		log.debug("ExternalDwhRestrictionController.set [request="+request+"]");

		validationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());

		User player;
		try {
			player = userApiInternalClientService.getUserByGuid(request.getUserGuid());
		} catch (UserNotFoundException | UserClientServiceFactoryException userNotFound) {
			throw new Status404UserNotFoundException("Player not found");
		}

		DomainRestrictionSet domainRestrictionSet;
		try {
			domainRestrictionSet = restrictionService.find(request.getRestrictionId());

			List<DomainRestrictionSet> dwhVisibleTrue = restrictionService.findByDomainNameAndEnabledTrueAndDwhVisibleTrue(request.getUserGuid().split("/")[0]);
			if (dwhVisibleTrue.stream().noneMatch(set -> request.getRestrictionId().equals(set.getId()))) {
				throw new Exception();
			}
		} catch (Exception notFoundException) {
			throw new Status404RestrictionNotFoundException("Restriction not found: The restriction ID has to be listed on the /get API to be found.");
		}

		try {
			userRestrictionService.place(player.guid(), domainRestrictionSet, request.getApiAuthorizationId(), "", player.getId(), request.getSubType(), null);
		}
		catch (Status422PlayerRestrictionExclusionException e) {
			log.error(String.format("Failed to place restriction on player %s, reason: %s", player.getGuid(), e.getMessage()));
			throw e;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return Response.<DwhRestrictionResponse>builder()
				.data(getDwhRestrictionResponse(domainRestrictionSet))
				.status(Response.Status.OK)
				.build();
	}

	private List<DwhRestrictionResponse> getDwhRestrictionResponseList(List<DomainRestrictionSet> dwhRestrictions) {
		return dwhRestrictions.stream().map(this::getDwhRestrictionResponse).collect(Collectors.toList());
	}

	private DwhRestrictionResponse getDwhRestrictionResponse(DomainRestrictionSet set) {
		return DwhRestrictionResponse.builder()
				.id(set.getId())
				.name(set.getName())
				.restrictions(set.getRestrictions().stream().map(restriction -> restriction.getRestriction().getName()).collect(Collectors.toList()))
				.build();
	}
}
