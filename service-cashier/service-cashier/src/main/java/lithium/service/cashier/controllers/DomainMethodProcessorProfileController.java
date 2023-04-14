package lithium.service.cashier.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProfile;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.DomainMethodProcessorProfileService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cashier/dmp/profile")
public class DomainMethodProcessorProfileController {
//	@Autowired
//	private DomainMethodProcessorService domainMethodProcessorService;
	@Autowired
	private DomainMethodProcessorProfileService domainMethodProcessorProfileService;
	
	@PostMapping
	@JsonView(Views.Public.class)
	public Response<?> create(
		@RequestBody DomainMethodProcessorProfile domainMethodProcessorProfile
	) throws Exception {
		log.debug("Create or Update DMP Profile link");
		return Response.<DomainMethodProcessorProfile>builder()
			.data(domainMethodProcessorProfileService.createOrUpdate(domainMethodProcessorProfile))
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainMethodProcessorProfile> domainMethodProcessorProfiles
	) throws Exception {
		List<DomainMethodProcessorProfile> dmpps = new ArrayList<>();
		domainMethodProcessorProfiles.forEach(dmpp -> {
			try {
				dmpps.add(domainMethodProcessorProfileService.createOrUpdate(dmpp));
			} catch (Exception e) {
				log.error("Could not create/update : "+dmpp);
			}
		});
		return Response.<List<DomainMethodProcessorProfile>>builder()
			.data(dmpps)
			.status(Status.OK)
			.build();
	}
	
//	@PostMapping("/fields")
//	@JsonView(Views.Public.class)
//	public Response<?> create(
//		@RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
//		@RequestParam("profileId") Long profileId,
//		@RequestParam("feeFlat") Long feeFlat,
//		@RequestParam("feePercentage") BigDecimal feePercentage,
//		@RequestParam("feeMinimum") Long feeMinimum,
//		@RequestParam("limitMinAmount") Long limitMinAmount,
//		@RequestParam("limitMaxAmount") Long limitMaxAmount,
//		@RequestParam("limitMaxAmountDay") Long limitMaxAmountDay,
//		@RequestParam("limitMaxAmountWeek") Long limitMaxAmountWeek,
//		@RequestParam("limitMaxAmountMonth") Long limitMaxAmountMonth,
//		@RequestParam("limitMaxTransactionsDay") Long limitMaxTransactionsDay,
//		@RequestParam("limitMaxTransactionsWeek") Long limitMaxTransactionsWeek,
//		@RequestParam("limitMaxTransactionsMonth") Long limitMaxTransactionsMonth,
//		@RequestParam("weight") Double weight,
//		@RequestParam("enabled") Boolean enabled
//	) throws Exception {
//		log.debug("Create or Update DMP Profile link");
//		return Response.<DomainMethodProcessorProfile>builder()
//			.data(
//				domainMethodProcessorProfileService.createOrUpdate(
//					domainMethodProcessorId,
//					profileId,
//					feeFlat,
//					feePercentage,
//					feeMinimum,
//					limitMinAmount,
//					limitMaxAmount,
//					limitMaxAmountDay,
//					limitMaxAmountWeek,
//					limitMaxAmountMonth,
//					limitMaxTransactionsDay,
//					limitMaxTransactionsWeek,
//					limitMaxTransactionsMonth,
//					weight,
//					enabled
//				)
//			)
//			.status(Status.OK)
//			.build();
//	}
	
	@JsonView(Views.Public.class)
	@GetMapping("/{domainMethodProcessorProfileId}")
	public Response<?> find(
		@PathVariable("domainMethodProcessorProfileId") Long domainMethodProcessorProfileId
	) {
		return Response.<DomainMethodProcessorProfile>builder()
			.data(
				domainMethodProcessorProfileService.find(domainMethodProcessorProfileId)
			)
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodProcessorProfileId}/image")
	public Response<?> findWithImage(
		@PathVariable("domainMethodProcessorProfileId") Long domainMethodProcessorProfileId
	) {
		return find(domainMethodProcessorProfileId);
	}
	
//	@JsonView(Views.Public.class)
//	@GetMapping("/bydmprofile/{domainMethodProfileId}")
//	public Response<?> findByDomainMethodProfile(
//		@PathVariable("domainMethodProfileId") DomainMethodProfile domainMethodProfile
//	) {
//		return Response.<List<DomainMethodProcessorProfile>>builder()
//			.data(domainMethodProcessorProfileService.findByDomainMethodProfile(domainMethodProfile))
//			.status(Status.OK)
//			.build();
//	}
//	@JsonView(Views.Image.class)
//	@GetMapping("/bydmprofile/{domainMethodProfileId}/image")
//	public Response<?> findByDomainMethodProfileWithImage(
//		@PathVariable("domainMethodProfileId") DomainMethodProfile domainMethodProfile
//	) {
//		return findByDomainMethodProfile(domainMethodProfile);
//	}
	
	@JsonView(Views.Public.class)
	@GetMapping("/byprofile/{profileId}")
	public Response<?> findByProfile(
		@PathVariable("profileId") Profile profile
	) {
		return Response.<List<DomainMethodProcessorProfile>>builder()
			.data(domainMethodProcessorProfileService.findByProfile(profile))
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/byprofile/{profileId}/image")
	public Response<?> findByProfileWithImage(
		@PathVariable("profileId") Profile profile
	) {
		return findByProfile(profile);
	}
	
	@JsonView(Views.Public.class)
	@GetMapping("/bydmp/{domainMethodProcessorId}")
	public Response<?> findByDMP(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return Response.<List<DomainMethodProcessorProfile>>builder()
			.data(
				domainMethodProcessorProfileService.findByDomainMethodProcessor(domainMethodProcessor)
			)
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/bydmp/{domainMethodProcessorId}/table")
	public DataTableResponse<?> findByDMPTable(
		DataTableRequest request,
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return new DataTableResponse<>(request, domainMethodProcessorProfileService.findByDomainMethodProcessor(request, domainMethodProcessor));
	}
	
	@PutMapping("/{domainMethodProcessorProfileId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> updateFees(
		@PathVariable("domainMethodProcessorProfileId") DomainMethodProcessorProfile domainMethodProcessorProfile,
		@RequestBody Fees fees
	) {
		return Response.<DomainMethodProcessorProfile>builder()
			.data(domainMethodProcessorProfileService.saveFees(domainMethodProcessorProfile, fees))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/{domainMethodProcessorProfileId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> updateLimits(
		@PathVariable("domainMethodProcessorProfileId") DomainMethodProcessorProfile domainMethodProcessorProfile,
		@RequestBody Limits limits
	) {
		return Response.<DomainMethodProcessorProfile>builder()
			.data(domainMethodProcessorProfileService.saveLimits(domainMethodProcessorProfile, limits))
			.status(Status.OK)
			.build();
	}
	
	@DeleteMapping("/{domainMethodProcessorProfileId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> deleteFees(
		@PathVariable("domainMethodProcessorProfileId") DomainMethodProcessorProfile domainMethodProcessorProfile
	) {
		return Response.<DomainMethodProcessorProfile>builder()
			.data(domainMethodProcessorProfileService.deleteFees(domainMethodProcessorProfile))
			.status(Status.OK)
			.build();
	}
	@DeleteMapping("/{domainMethodProcessorProfileId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> deleteLimits(
		@PathVariable("domainMethodProcessorProfileId") DomainMethodProcessorProfile domainMethodProcessorProfile
	) {
		return Response.<DomainMethodProcessorProfile>builder()
			.data(domainMethodProcessorProfileService.deleteLimits(domainMethodProcessorProfile))
			.status(Status.OK)
			.build();
	}
}