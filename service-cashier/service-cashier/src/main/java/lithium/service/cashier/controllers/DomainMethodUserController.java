package lithium.service.cashier.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodUser;
import lithium.service.cashier.services.DomainMethodUserService;

@RestController
@RequestMapping("/cashier/dm/user")
public class DomainMethodUserController {
	@Autowired
	private DomainMethodUserService domainMethodUserService;
	
	@GetMapping("/{domainMethodId}")
	public Response<?> find(
		@PathVariable("domainMethodId") DomainMethod domainMethod,
		@RequestParam("userGuid") String userGuid
	) {
		return Response.<DomainMethodUser>builder()
			.data(domainMethodUserService.find(domainMethod, userGuid))
			.status(Status.OK)
			.build();
	}
	@PutMapping
	public Response<?> update(
		@RequestBody DomainMethodUser domainMethodUser
	) {
		return Response.<DomainMethodUser>builder()
			.data(domainMethodUserService.createOrUpdate(domainMethodUser))
			.status(Status.OK)
			.build();
	}
	@PutMapping("/multiple")
	public Response<?> updateMultiple(
		@RequestBody List<DomainMethodUser> domainMethodUsers
	) {
		domainMethodUsers.forEach(dmuser -> {
			domainMethodUserService.createOrUpdate(dmuser);
		});
		return Response.<List<DomainMethodUser>>builder()
			.data(domainMethodUsers)
			.status(Status.OK)
			.build();
	}
}
