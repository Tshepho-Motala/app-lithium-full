package lithium.service.cashier.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.services.ProfileService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("cashierProfileController")
@RequestMapping("/cashier/profile/{domainName}")
public class ProfileController {
	@Autowired
	private ProfileService profileService;
	
	@PostMapping
	public Response<?> save(
		@PathVariable("domainName") String domainName,
		@RequestBody Profile profile
	) {
		return Response.<Profile>builder()
			.data(profileService.save(profile))
			.status(Status.OK)
			.build();
	}
	
	@PostMapping("/fields")
	public Response<?> create(
		@PathVariable("domainName") String domainName,
		@RequestParam("code") String code,
		@RequestParam("name") String name,
		@RequestParam(name="description", required=false) String description,
		@RequestParam(name="totalDeposits", required=false) Long totalDeposits,
		@RequestParam(name="totalPayouts", required=false) Long totalPayouts,
		@RequestParam(name="numberDeposits", required=false) Integer numberDeposits,
		@RequestParam(name="numberPayouts", required=false) Integer numberPayouts,
		@RequestParam(name="accountActiveDays", required=false) Integer accountActiveDays
	) {
		return Response.<Profile>builder()
			.data(
				profileService.save(domainName, code, name, description, totalDeposits, totalPayouts, numberDeposits, numberPayouts, accountActiveDays)
			)
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/{profileId}")
	public Response<?> update(
		@PathVariable("domainName") String domainName,
		@PathVariable("profileId") Long profileId,
		@RequestParam("code") String code,
		@RequestParam("name") String name,
		@RequestParam(name="description", required=false) String description,
		@RequestParam(name="totalDeposits", required=false) Long totalDeposits,
		@RequestParam(name="totalPayouts", required=false) Long totalPayouts,
		@RequestParam(name="numberDeposits", required=false) Integer numberDeposits,
		@RequestParam(name="numberPayouts", required=false) Integer numberPayouts,
		@RequestParam(name="accountActiveDays", required=false) Integer accountActiveDays
	) {
		log.debug("updating profile.");
		return Response.<Profile>builder()
			.data(
				profileService.update(profileId, code, name, description, totalDeposits, totalPayouts, numberDeposits, numberPayouts, accountActiveDays)
			)
			.status(Status.OK)
			.build();
	}
	
	@GetMapping
	public Response<?> profilesByDomain(@PathVariable("domainName") String domainName) {
		log.debug("find all profiles by domain.");
		return Response.<List<Profile>>builder()
			.data(
				profileService.findByDomainName(domainName)
			)
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/{profileId}")
	public Response<?> profilesById(@PathVariable("profileId") Profile profile) {
		return Response.<Profile>builder()
			.data(profile)
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/code")
	public Response<?> findByDomainNameAndCode(
		@PathVariable("domainName") String domainName,
		@RequestParam("code") String code
	) {
		return Response.<Profile>builder()
			.data(
				profileService.findByDomainNameAndCode(domainName, code)
			)
			.status(Status.OK)
			.build();
	}
	
	@PostMapping("/{id}/delete")
	public Response<?> delete(
		@PathVariable("domainName") String domainName,
		@PathVariable("id") Profile profile,
		LithiumTokenUtil tokenUtil
	) {
		try {
			return Response.<List<Profile>>builder().data(profileService.delete(domainName, profile)).status(Status.OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<List<Profile>>builder().data(null).status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}