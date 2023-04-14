package lithium.service.user.controllers;

import java.security.Principal;
import javax.validation.Valid;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{domain}/players/{id}")
@Slf4j
public class PlayerController {
	private final UserService userService;

  public PlayerController(UserService userService) {
    this.userService = userService;
  }

//	@GetMapping("/401")
//	public Response<User> return401(HttpServletResponse response) {
//		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//		return Response.<User>builder().status(Status.UNAUTHORIZED).message("server message").build();
//	}
//	@GetMapping("/403")
//	public Response<User> return403(HttpServletResponse response) {
//		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//		return Response.<User>builder().status(Status.FORBIDDEN).message("server message").build();
//	}
//	@GetMapping("/404")
//	public Response<User> return404(HttpServletResponse response) {
//		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//		return Response.<User>builder().status(Status.NOT_FOUND).message("server message").build();
//	}
//	@GetMapping("/409")
//	public Response<User> return409(HttpServletResponse response) {
//		response.setStatus(HttpServletResponse.SC_CONFLICT);
//		return Response.<User>builder().status(Status.CONFLICT).message("server message").build();
//	}
//	@GetMapping("/500")
//	public Response<User> return500(HttpServletResponse response) {
//		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		return Response.<User>builder().status(Status.INTERNAL_SERVER_ERROR).message("server message").build();
//	}
	
	
	@GetMapping
	private Response<User> get(@PathVariable("domain") String domain, @PathVariable("id") User user) {
		if (user == null) return Response.<User>builder().status(Status.NOT_FOUND).build();
		if (!user.getDomain().getName().equals(domain)) return Response.<User>builder().status(Status.NOT_FOUND).build();
		// Never send the password over the wire.
		user.clearPassword();
		return Response.<User>builder().data(user).build();
	}
	
	@PutMapping
	private Response<User> save(@PathVariable("domain") String domain, @PathVariable("id") User user, @RequestBody @Valid PlayerBasic userUpdate, BindingResult bindingResult, Principal principal) throws Exception {
    if (ObjectUtils.isEmpty(user) || !ObjectUtils.nullSafeEquals(domain, user.getDomain().getName())) {
      return Response.<User>builder().status(Status.NOT_FOUND).build();
    }

		return Response.<User>builder().data(userService.updatePlayer(domain, user, userUpdate, principal)).build();
	}

	@PostMapping(value="/changepassword")
	private Response<User> changePassword(@PathVariable("domain") String domain, @PathVariable("id") User user, @RequestBody String password, Principal principal) throws Exception {
		if (user == null) return Response.<User>builder().status(Status.NOT_FOUND).build();
		if (!user.getDomain().getName().equals(domain)) return Response.<User>builder().status(Status.NOT_FOUND).build();

    user = userService.changePassword(domain, user, password, principal);

		return Response.<User>builder().data(user).build();
	}
}
