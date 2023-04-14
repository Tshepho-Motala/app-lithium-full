package lithium.service.user.controllers;

import lithium.service.Response;
import lithium.service.user.data.entities.IncompleteUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{domain}/incompleteusers/{id}")
@Slf4j
public class IncompleteUserController {
	@GetMapping
	public Response<IncompleteUser> get(
		@PathVariable("domain") String domainName,
		@PathVariable("id") IncompleteUser incompleteUser
	) {
		return Response.<IncompleteUser>builder().data(incompleteUser).status(Response.Status.OK).build();
	}
}
