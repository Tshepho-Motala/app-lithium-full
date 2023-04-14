package lithium.service.cashier.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.Profile;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.services.UserService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cashier/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping
	public Response<?> findOrCreate(
		@RequestParam("userGuid") String userGuid
	) {
		log.debug("find all profiles by domain.");
		return Response.<User>builder()
			.data(
				userService.findOrCreate(userGuid)
			)
			.status(Status.OK)
			.build();
	}
	
	@GetMapping("/profile/{profileId}/table")
	public DataTableResponse<?> findByProfileIdDT(
		DataTableRequest request,
		@PathVariable("profileId") Profile profile
	) {
		return new DataTableResponse<>(request, userService.findByProfile(request, profile));
	}
	
	@GetMapping("/profile/limits")
	public Response<?>  processorUserLimits(
 			 @RequestParam(name="userGuid") String userGuid) {
		return new Response<>(userService.find(userGuid).getLimits());
	}
	
	
	@PostMapping("/limits")
	public Response<?> limits(
		@RequestParam(name="userGuid") String userGuid,
		@RequestParam(name="minAmount", required=false) Long minAmount,
		@RequestParam(name="maxAmount", required=false) Long maxAmount,
		@RequestParam(name="maxAmountDay", required=false) Long maxAmountDay,
		@RequestParam(name="maxAmountWeek", required=false) Long maxAmountWeek,
		@RequestParam(name="maxAmountMonth", required=false) Long maxAmountMonth,
		@RequestParam(name="maxTransactionsDay", required=false) Long maxTransactionsDay,
		@RequestParam(name="maxTransactionsWeek", required=false) Long maxTransactionsWeek,
		@RequestParam(name="maxTransactionsMonth", required=false) Long maxTransactionsMonth,
		@RequestParam(name="minFirstTransactionAmount", required=false) Long minFirstTransactionAmount,
		@RequestParam(name="maxFirstTransactionAmount", required=false) Long maxFirstTransactionAmount
	) {
		return Response.<User>builder()
		.data(
			userService.createLimits(userGuid, minAmount, maxAmount, minFirstTransactionAmount, maxFirstTransactionAmount,maxAmountDay, maxAmountWeek, maxAmountMonth, maxTransactionsDay, maxTransactionsWeek, maxTransactionsMonth)
		)
		.status(Status.OK)
		.build();
	}
	
	@DeleteMapping("/limits")
	public Response<?> removeLimits(
		@RequestParam(name="userGuid") String userGuid
	) {
		return Response.<User>builder()
			.data(
				userService.removeLimits(userGuid)
			)
			.status(Status.OK)
			.build();
	}
	
	@PutMapping("/profile")
	public Response<?> update(
		@RequestParam("userGuid") String userGuid,
		@RequestParam("profileId") Long profileId
	) throws Exception {
		return Response.<User>builder()
			.data(
				userService.createOrUpdate(userGuid, profileId)
			)
			.status(Status.OK)
			.build();
	}
}
