package lithium.service.limit.controllers.backoffice;

import java.util.List;
import java.util.stream.Collectors;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/user-restrictions/{domainName}")
@Slf4j
public class BackofficeUserRestrictionsController {

	@Autowired
	private RestrictionService restrictionService;
	@Autowired
	private UserRestrictionService service;
	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public Response<List<lithium.service.limit.objects.UserRestrictionSet>> get(
			@PathVariable("domainName") String domainName,
			@RequestParam("userGuid") String userGuid) {
		try {
			DomainValidationUtil.validate(userGuid.split("/")[0], domainName);
			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder()
					.data(service.getActiveUserRestrictions(userGuid, null)
							.stream().map(ur -> lithium.service.limit.objects.UserRestrictionSet.builder()
									.id(ur.getId())
									.user(ur.getUser())
									.set(ur.getSet())
									.createdOn(new DateTime(ur.getCreatedOn()).withZone(DateTimeZone.UTC).toString())
									.activeFrom(
											new DateTime(ur.getActiveFrom()).withZone(DateTimeZone.UTC).toString())
									.activeTo(new DateTime(ur.getActiveTo()).withZone(DateTimeZone.UTC).toString())
									.active(service.isAppliedUserRestriction(ur))
									.subType(ur.getSubType())
									.displayName(getDisplayName(ur))
									.build()).collect(Collectors.toList())

					)
					.status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to get user restrictions [userGuid=" + userGuid + "] " + e.getMessage(), e);
			return Response.<List<lithium.service.limit.objects.UserRestrictionSet>>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	@GetMapping("/getEligibleRestrictionSetsForUser")
	public Response<List<DomainRestrictionSet>> getEligibleRestrictionSetsForUser(
			@PathVariable("domainName") String domainName,
			@RequestParam("userGuid") String userGuid
	) throws Status500InternalServerErrorException {
		try {
			DomainValidationUtil.validate(userGuid.split("/")[0], domainName);
			return Response.<List<DomainRestrictionSet>>builder()
					.data(service.getEligibleRestrictionSetsForUser(userGuid))
					.status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to get eligible restriction sets for user [userGuid=" + userGuid + "] "
					+ e.getMessage(), e);
			return Response.<List<DomainRestrictionSet>>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}

	@PostMapping("/set")
	public Response<lithium.service.limit.objects.UserRestrictionSet> set(
			@PathVariable("domainName") String domainName,
			@RequestParam("userGuid") String userGuid,
			@RequestParam("domainRestrictionSetId") Long domainRestrictionSetId,
			@RequestParam("userId") long userId,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "subType", required = false) Integer subType,
			LithiumTokenUtil tokenUtil
	) throws Exception {
		DomainValidationUtil.validate(userGuid.split("/")[0], domainName);
		DomainRestrictionSet set = restrictionService.find(domainRestrictionSetId);
		UserRestrictionSet userRestrictionSet = service.place(userGuid, set, tokenUtil.guid(), comment,
				userId, subType, tokenUtil);

		if (userRestrictionSet == null) {
			return Response.<lithium.service.limit.objects.UserRestrictionSet>builder()
					.status(Response.Status.EXISTS)
					.message(Response.Status.EXISTS.message()).build();
		}
		return Response.<lithium.service.limit.objects.UserRestrictionSet>builder()
				.data(lithium.service.limit.objects.UserRestrictionSet.builder()
						.id(userRestrictionSet.getId())
						.user(userRestrictionSet.getUser())
						.set(userRestrictionSet.getSet())
						.createdOn(new DateTime(userRestrictionSet.getCreatedOn()).withZone(DateTimeZone.UTC)
								.toString())
						.activeFrom(new DateTime(userRestrictionSet.getActiveFrom()).withZone(DateTimeZone.UTC)
								.toString())
						.activeTo(new DateTime(userRestrictionSet.getActiveTo()).withZone(DateTimeZone.UTC)
								.toString())
						.active(service.isAppliedUserRestriction(userRestrictionSet))
						.subType(userRestrictionSet.getSubType())
						.displayName(getDisplayName(userRestrictionSet))
						.build()).status(Response.Status.OK_SUCCESS).build();

	}


	@PostMapping("/lift")
	public Response<Void> lift(
			@PathVariable("domainName") String domainName,
			@RequestParam("userGuid") String userGuid,
			@RequestParam("userRestrictionSetId") Long userRestrictionSetId,
			@RequestParam("userId") long userId,
			@RequestParam(value = "comment", required = false) String comment,
			LithiumTokenUtil tokenUtil
	) {
		try {
			DomainValidationUtil.validate(userGuid.split("/")[0], domainName);
			UserRestrictionSet set = service.find(userRestrictionSetId);
			service.lift(userGuid, set.getSet(), tokenUtil.guid(), comment, userId, tokenUtil);
			return Response.<Void>builder().status(Response.Status.OK).build();
		} catch (Exception e) {
			log.error("Failed to lift the restriction set from user account [userGuid=" + userGuid
					+ ", userRestrictionSetId=" + userRestrictionSetId + "] " + e.getMessage(), e);
			return Response.<Void>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
					.message(e.getMessage()).build();
		}
	}

	private String getDisplayName(UserRestrictionSet userRestrictionSet) {
		String name = userRestrictionSet.getSet().getName().replace(" ", ".").trim();
		Integer subType = userRestrictionSet.getSubType();
		if (userRestrictionSet.getSet() != null && userRestrictionSet.getSet().isSystemRestriction()) {
			if (subType != null) {
				String translationKey =
						"UI_NETWORK_ADMIN.RESTRICTIONS.PLAYERS." + name.trim() + "." + subType + ".NAME";
				return translationKey.toUpperCase();
			} else {
				String translationKey = "UI_NETWORK_ADMIN.RESTRICTIONS.PLAYERS." + name.trim() + "." + "NA";
				return translationKey.toUpperCase();
			}
		} else {
			return userRestrictionSet.getSet()!=null?userRestrictionSet.getSet().getName().toUpperCase():"";
		}
	}
}
