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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorUser;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.DomainMethodProcessorUserService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cashier/dmp/user")
public class DomainMethodProcessorUserController {
	@Autowired
	private DomainMethodProcessorUserService domainMethodProcessorUserService;
	
	@PostMapping
	@JsonView(Views.Public.class)
	public Response<?> create(
		@RequestBody DomainMethodProcessorUser domainMethodProcessorUser
		) throws Exception {
		log.debug("Create or Update DMP User link");
		return Response.<DomainMethodProcessorUser>builder()
			.data(domainMethodProcessorUserService.createOrUpdate(domainMethodProcessorUser))
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainMethodProcessorUser> domainMethodProcessorUsers
	) throws Exception {
		List<DomainMethodProcessorUser> dmpus = new ArrayList<>();
		domainMethodProcessorUsers.forEach(dmpu -> {
			try {
				dmpus.add(domainMethodProcessorUserService.createOrUpdate(dmpu));
			} catch (Exception e) {
				log.error("Could not create/update : "+dmpu);
			}
		});
		return Response.<List<DomainMethodProcessorUser>>builder()
			.data(dmpus)
			.status(Status.OK)
			.build();
	}
	
//	@PostMapping("/fields")
//	@JsonView(Views.Public.class)
//	public Response<?> create(
//		@RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
//		@RequestParam("userGuid") String userGuid,
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
//		log.debug("Create or Update DMP User link");
//		return Response.<DomainMethodProcessorUser>builder()
//			.data(
//				domainMethodProcessorUserService.createOrUpdate(
//					domainMethodProcessorId,
//					userGuid,
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
	@GetMapping("/{domainMethodProcessorUserId}")
	public Response<?> find(
		@PathVariable("domainMethodProcessorUserId") Long domainMethodProcessorUserId
	) {
		return Response.<DomainMethodProcessorUser>builder()
			.data(
				domainMethodProcessorUserService.find(domainMethodProcessorUserId)
			)
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodProcessorUserId}/image")
	public Response<?> findWithImage(
		@PathVariable("domainMethodProcessorUserId") Long domainMethodProcessorUserId
	) {
		return find(domainMethodProcessorUserId);
	}
	
	@JsonView(Views.Public.class)
	@GetMapping("/byuser")
	public Response<?> findByUser(
		@RequestParam("userGuid") String userGuid
	) {
		return Response.<List<DomainMethodProcessorUser>>builder()
			.data(
				domainMethodProcessorUserService.findByUserGuid(userGuid)
			)
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/byuser/image")
	public Response<?> findByUserWithImage(
		@RequestParam("userGuid") String userGuid
	) {
		return findByUser(userGuid);
	}
	
	@JsonView(Views.Public.class)
	@GetMapping("/{domainMethodProcessorId}/bydmp")
	public Response<?> findByDMP(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return Response.<List<DomainMethodProcessorUser>>builder()
			.data(
				domainMethodProcessorUserService.findByDomainMethodProcessor(domainMethodProcessor)
			)
			.status(Status.OK)
			.build();
	}
	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodProcessorId}/bydmp/image")
	public Response<?> findByDMPWithImage(
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return findByDMP(domainMethodProcessor);
	}
	@JsonView(Views.Image.class)
	@GetMapping("/{domainMethodProcessorId}/bydmp/table")
	public DataTableResponse<?> findByDMPTable(
		DataTableRequest request,
		@PathVariable("domainMethodProcessorId") DomainMethodProcessor domainMethodProcessor
	) {
		return new DataTableResponse<>(request, domainMethodProcessorUserService.findByDomainMethodProcessor(request, domainMethodProcessor));
	}
	
	@PutMapping("/{domainMethodProcessorUserId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> updateFees(
		@PathVariable("domainMethodProcessorUserId") DomainMethodProcessorUser domainMethodProcessorUser,
		@RequestBody Fees fees
	) {
		return Response.<DomainMethodProcessorUser>builder()
			.data(domainMethodProcessorUserService.saveFees(domainMethodProcessorUser, fees))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/{domainMethodProcessorUserId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> updateLimits(
		@PathVariable("domainMethodProcessorUserId") DomainMethodProcessorUser domainMethodProcessorUser,
		@RequestBody Limits limits
	) {
		return Response.<DomainMethodProcessorUser>builder()
			.data(domainMethodProcessorUserService.saveLimits(domainMethodProcessorUser, limits))
			.status(Status.OK)
			.build();
	}
	

	@DeleteMapping("/{domainMethodProcessorUserId}/fees")
	@JsonView(Views.Public.class)
	public Response<?> deleteFees(
		@PathVariable("domainMethodProcessorUserId") DomainMethodProcessorUser domainMethodProcessorUser
	) {
		return Response.<DomainMethodProcessorUser>builder()
			.data(domainMethodProcessorUserService.deleteFees(domainMethodProcessorUser))
			.status(Status.OK)
			.build();
	}
	@DeleteMapping("/{domainMethodProcessorUserId}/limits")
	@JsonView(Views.Public.class)
	public Response<?> deleteLimits(
		@PathVariable("domainMethodProcessorUserId") DomainMethodProcessorUser domainMethodProcessorUser
	) {
		return Response.<DomainMethodProcessorUser>builder()
			.data(domainMethodProcessorUserService.deleteLimits(domainMethodProcessorUser))
			.status(Status.OK)
			.build();
	}
}