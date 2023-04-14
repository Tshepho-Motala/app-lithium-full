package lithium.service.limit.controllers.system;


import lithium.service.limit.client.DomainRestrictionsSystemClient;
import lithium.service.limit.client.objects.DomainRestriction;

import lithium.service.limit.services.RestrictionService;
import lithium.service.limit.services.UserRestrictionService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/restrictions")
@Slf4j
public class DomainRestrictionsSystemController implements DomainRestrictionsSystemClient {
	@Autowired private RestrictionService restrictionService;
	@Autowired private UserRestrictionService userRestrictionService;

	@GetMapping("/domain-restrictions")
	public List<DomainRestriction> getDomainRestrictions(@RequestParam("domainName") String domainName) {
		return restrictionService.findByDomainNameAndEnabledTrue(domainName).stream().map(rs -> DomainRestriction.builder().name(rs.getName()).build()).collect(Collectors.toList());
	}
	@GetMapping("/user-domain-restrictions")
	public List<DomainRestriction> getUserDomainRestrictions(@RequestParam("userGuid") String userGuid) {
		return userRestrictionService.getActiveUserRestrictions(userGuid, new DateTime()).stream().map(ur -> DomainRestriction.builder().name(ur.getSet().getName()).build()).collect(Collectors.toList());
	}
}
