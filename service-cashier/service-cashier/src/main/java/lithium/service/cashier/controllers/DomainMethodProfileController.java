package lithium.service.cashier.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProfile;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.services.DomainMethodProfileService;

@RestController
@RequestMapping("/cashier/dm/profile")
public class DomainMethodProfileController {
	@Autowired
	private DomainMethodProfileService domainMethodProfileService;
	
	@GetMapping("/{domainMethodId}/{profileId}")
	public Response<?> find(
		@PathVariable("domainMethodId") DomainMethod domainMethod,
		@PathVariable("profileId") Profile profile
	) {
		return Response.<DomainMethodProfile>builder()
			.data(domainMethodProfileService.find(domainMethod, profile))
			.status(Status.OK)
			.build();
	}
	@PutMapping
	public Response<?> update(
		@RequestBody DomainMethodProfile domainMethodProfile
	) {
		return Response.<DomainMethodProfile>builder()
			.data(domainMethodProfileService.createOrUpdate(domainMethodProfile))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainMethodProfile> domainMethodProfiles
	) {
		domainMethodProfiles.forEach(dmprofile -> {
			domainMethodProfileService.createOrUpdate(dmprofile);
		});
		return Response.<List<DomainMethodProfile>>builder()
			.data(domainMethodProfiles)
			.status(Status.OK)
			.build();
	}
}
