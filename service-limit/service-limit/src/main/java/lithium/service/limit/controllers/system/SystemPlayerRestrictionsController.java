package lithium.service.limit.controllers.system;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.Response;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.Restrictions;
import lithium.service.limit.client.objects.VerificationStatusDto;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.objects.UserRestrictionSet;
import lithium.service.limit.objects.UserRestrictionsRequest;
import lithium.service.limit.services.AutoRestrictionService;
import lithium.service.limit.services.CoolOffService;
import lithium.service.limit.services.ExclusionMessageService;
import lithium.service.limit.services.ExclusionService;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lithium.service.limit.services.VerificationStatusService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/restrictions")
@Slf4j
public class SystemPlayerRestrictionsController {
	@Autowired private CoolOffService coolOffService;
	@Autowired private ExclusionService exclusionService;
	@Autowired private ExclusionMessageService messages;
	@Autowired private ModelMapper modelMapper;
	@Autowired private UserRestrictionService userRestrictionService;
	@Autowired private VerificationStatusService verificationStatusService;
	@Autowired private RestrictionService restrictionService;
	@Autowired private AutoRestrictionService autoRestrictionService;

	@GetMapping
	@TimeThisMethod
	public Restrictions lookupRestrictions(@RequestParam("playerGuid") String playerGuid, Locale locale) {
		Restrictions restrictions = Restrictions.builder().build();
		SW.start("coolOffService");
		PlayerCoolOff playerCoolOff = coolOffService.lookup(playerGuid);
		if (playerCoolOff != null) {
			lithium.service.limit.client.objects.PlayerCoolOff playerCoolOffCO =
				new lithium.service.limit.client.objects.PlayerCoolOff();
			modelMapper.map(playerCoolOff, playerCoolOffCO);
			restrictions.setPlayerCoolOff(playerCoolOffCO);
		}
		SW.stop();
		SW.start("exclusionService");
		PlayerExclusionV2 playerExclusionV2 = exclusionService.lookup(playerGuid);
		if (playerExclusionV2 != null) {
			lithium.service.limit.client.objects.PlayerExclusionV2 playerExclusionV2CO =
				new lithium.service.limit.client.objects.PlayerExclusionV2();
			modelMapper.map(playerExclusionV2, playerExclusionV2CO);
			messages.populateMessage(playerExclusionV2CO, locale);
			restrictions.setPlayerExclusionV2(playerExclusionV2CO);
		}
		SW.stop();

//		Access access = userRestrictionService.checkAccess(playerGuid);
//		restrictions.setAccess(access);

		return restrictions;
	}

	@GetMapping("/checkAccess")
	public Access checkAccess(@RequestParam("playerGuid") String playerGuid) throws Status500InternalServerErrorException {
		return userRestrictionService.checkAccess(playerGuid);
	}

	@PostMapping("/check-access-localized")
	public Access checkAccess(@RequestParam("playerGuid") String playerGuid, @RequestParam("locale") String locale) throws Status500InternalServerErrorException {
		return userRestrictionService.checkAccess(playerGuid);
	}

    @GetMapping("/getVerificationStatusCode")
    public String getVerificationStatusCode(@RequestParam("verificationStatusId") Long verificationStatusId) throws Status500InternalServerErrorException {
	    return verificationStatusService.getVerificationStatus(verificationStatusId).getCode();
    }

    @GetMapping("/getVerificationStatusLevel")
    public Integer getVerificationStatusLevel(@RequestParam("verificationStatusId") Long verificationStatusId) throws Status500InternalServerErrorException {
	    return verificationStatusService.getVerificationStatus(verificationStatusId).getLevel();
    }

	@RequestMapping(method = RequestMethod.GET, path = "/get-verification-status-level-age-override")
	Integer getVerificationStatusLevelAgeOverride(@RequestParam("verificationStatusId") Long verificationStatusId, @RequestParam("domainName") String domainName) throws Status500InternalServerErrorException, Status550ServiceDomainClientException {
		return verificationStatusService.getVerificationStatusLevel(verificationStatusId, domainName);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/get-all-verification-status")
	public List<VerificationStatusDto> getVerificationStatuses() {
		return verificationStatusService.getAllVerificationStatuses();
	}

    @PostMapping("/{domainName}/set-promotions-opt-out")
    public void setPromotionsOptOut(@PathVariable("domainName") String domainName, @RequestParam("playerGuid") String playerGuid, @RequestParam("optOut") boolean optOut, @RequestParam("userId") Long userId, LithiumTokenUtil util) throws Status500InternalServerErrorException, Status422PlayerRestrictionExclusionException, Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {

		DomainRestrictionSet domainRestrictionSet = restrictionService.findByDomainAndName(domainName, SystemRestriction.PLAYER_COMPS_OPTOUT.restrictionName());

		if(optOut) {
			log.info("Will place a restriction");
			userRestrictionService.place(playerGuid, domainRestrictionSet, util.guid(), null, userId, null, util);
		}
		else {
			log.info("Will lift a restriction");
			userRestrictionService.lift(playerGuid, domainRestrictionSet, util.guid(), null, userId, util);
		}
	}

	@RequestMapping(value = "/set-many", method = RequestMethod.POST)
	public Response<List<UserRestrictionSet>> setMany(@Valid @RequestBody UserRestrictionsRequest userRestrictionsRequest, LithiumTokenUtil util) {

		try {

			Map<String, String> failedSet = new HashMap<>();
			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder()
					.data(userRestrictionService.placeMany(userRestrictionsRequest, util, failedSet)
							.stream().map(ur -> lithium.service.limit.objects.UserRestrictionSet.builder()
									.id(ur.getId())
									.user(ur.getUser())
									.set(ur.getSet())
									.createdOn(new DateTime(ur.getCreatedOn()).withZone(DateTimeZone.UTC).toString())
									.activeFrom(new DateTime(ur.getActiveFrom()).withZone(DateTimeZone.UTC).toString())
									.activeTo(new DateTime(ur.getActiveTo()).withZone(DateTimeZone.UTC).toString())
									.active(userRestrictionService.isAppliedUserRestriction(ur))
									.build()).collect(Collectors.toList()))
					.data2(failedSet)
					.build();

		}
		catch (Exception e) {
			log.error("Failed to apply restrictionSets to user account [userGuid="+userRestrictionsRequest.getUserGuid()
					+ ", domainRestrictionSetIds="+userRestrictionsRequest.getDomainRestrictionSets().toString()+"] " + e.getMessage(), e);
			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@RequestMapping(value = "/lift-many", method = RequestMethod.DELETE)
	public Response<List<lithium.service.limit.objects.UserRestrictionSet>> liftMany(@Valid @RequestBody UserRestrictionsRequest userRestrictionsRequest, LithiumTokenUtil util) {
		try {

			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder()
					.data(userRestrictionService.liftMany(userRestrictionsRequest, util)
							.stream().map(ur -> lithium.service.limit.objects.UserRestrictionSet.builder()
									.id(ur.getId())
									.user(ur.getUser())
									.set(ur.getSet())
									.createdOn(new DateTime(ur.getCreatedOn()).withZone(DateTimeZone.UTC).toString())
									.activeFrom(new DateTime(ur.getActiveFrom()).withZone(DateTimeZone.UTC).toString())
									.activeTo(new DateTime(ur.getActiveTo()).withZone(DateTimeZone.UTC).toString())
									.active(userRestrictionService.isAppliedUserRestriction(ur))
									.build()).collect(Collectors.toList()))
					.build();

		}
		catch (Exception e) {
			log.error("Failed to lift restrictionSets to user account [userGuid="+userRestrictionsRequest.getUserGuid()
					+ ", domainRestrictionSetIds="+userRestrictionsRequest.getDomainRestrictionSets().toString()+"] " + e.getMessage(), e);
			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();

		}
	}

	@RequestMapping(value = "/auto-restriction/trigger", method = RequestMethod.POST)
	public void autoRestrictionTrigger(@RequestParam("playerGuid") String playerGuid) {
		autoRestrictionService.processAutoRestrictionRulesets(playerGuid, false, false);
	}
}
