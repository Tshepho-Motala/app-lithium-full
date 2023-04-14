package lithium.service.user.api.frontend.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.IncompleteUserBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PlayerRegistrationMethods;
import lithium.service.user.services.IncompleteUserService;
import lithium.service.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/frontend/players")
public class FrontendPlayersController {
	@Autowired private UserService userService;
  @Autowired private IncompleteUserService incompleteUserService;

	@PostMapping("/{domainName}/isfullnameunique")
	public Response<Boolean> isfullnameunique(@PathVariable("domainName") String domainName,
											  @RequestParam("firstName") String firstName,
											  @RequestParam("lastName") String lastName,
											  @RequestParam("dobDay") int dobDay,
											  @RequestParam("dobMonth") int dobMonth,
											  @RequestParam("dobYear") int dobYear) {
		boolean isUnique = userService.isFullNameUnique(domainName, firstName,
			lastName, dobDay, dobMonth, dobYear);
		return Response.<Boolean>builder().status(OK).data(isUnique).build();
	}

	@PostMapping("/{domainName}/v2/is-full-name-unique")
	public Response<Boolean> isFullNameUniquev2(@PathVariable("domainName") String domainName,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("dobDay") int dobDay,
			@RequestParam("dobMonth") int dobMonth,
			@RequestParam("dobYear") int dobYear,
			@RequestParam(name = "ecosystemAware", required = false, defaultValue = "false") Boolean ecosystemAware) {
		boolean isUnique = userService.isFullNameUnique(domainName, firstName,
				lastName, dobDay, dobMonth, dobYear, !ecosystemAware);
		return Response.<Boolean>builder().status(OK).data(isUnique).build();
	}

	@PostMapping("/{domainName}/v2/is-username-unique")
	public Response<Boolean> isUsernamenameUniqueV2(@PathVariable("domainName") String domainName,
			@RequestParam("username") String username,
			@RequestParam(name = "ecosystemAware", required = false, defaultValue = "false") Boolean ecosystemAware) {
		boolean isUnique = userService.isUniqueUsername(domainName, username, !ecosystemAware);
		return Response.<Boolean>builder().status(OK).data(isUnique).build();
	}

	@PostMapping("/{domainName}/v2/is-email-unique")
	public Response<Boolean> isEmailUniqueV2(@PathVariable("domainName") String domainName,
			@RequestParam("email") String email,
			@RequestParam(name = "ecosystemAware", required = false, defaultValue = "false") Boolean ecosystemAware) {
		boolean isUnique = userService.isUniqueEmail(domainName, email, !ecosystemAware);
		return Response.<Boolean>builder().status(OK).data(isUnique).build();
	}

  @PostMapping("/{domainName}/register/incomplete/v1")
  public Response<PlayerBasic> createIncomplete(
      @PathVariable("domainName") String domainName,
      @RequestBody IncompleteUserBasic incompleteUserBasic,
      @RequestParam(required = false) String method,
      @RequestParam(required = false) String sha,
      @RequestParam(name = "apiAuthorizationId", defaultValue = "ls-gw", required = false) String apiAuthorizationId,
      @RequestHeader("Authorization") String authorization)
      throws Exception {
    if (method != null && !method.isEmpty() && !method.equalsIgnoreCase(PlayerRegistrationMethods.IDIN.getMethod())){
      Status methodNotFound = NOT_FOUND.message("Method Not Found");
      return Response.<PlayerBasic>builder().status(methodNotFound).message(methodNotFound.message()).build();
    }
    return incompleteUserService.registerIncompleteUser(domainName, incompleteUserBasic, sha, apiAuthorizationId, authorization);
  }
}
