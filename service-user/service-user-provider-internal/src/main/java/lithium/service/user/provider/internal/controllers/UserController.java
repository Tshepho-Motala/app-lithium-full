package lithium.service.user.provider.internal.controllers;

import java.security.Principal;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.provider.internal.data.entities.User;
import lithium.service.user.provider.internal.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
	
	@Autowired UserRepository users;
	
	@RequestMapping(path = "/auth")
	public Response<User> auth(@RequestParam String domain, @RequestParam String username, @RequestParam String password, @RequestParam(required=false) Map<String, String> extraParameters) {
		log.info("auth domain " + domain + " username " + username + " password " + password);
		User user = users.findByDomainAndUsernameIgnoreCase(domain, username);
		if (user == null) {
			log.warn("User not found.");
			return Response.<User>builder().status(Response.Status.NOT_FOUND).build();
		}
		if (!user.getPassword().equals(password)) {
			log.warn("User not authorized. "+user);
			return Response.<User>builder().status(Response.Status.UNAUTHORIZED).build();
		}
//		try {
//			Thread.sleep(5000L);
//		} catch (Exception e) {
//			
//		}
		log.info("auth successful : "+user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}

	@RequestMapping(path = "/user")
	public Response<User> user(@RequestParam String domain, @RequestParam String username, @RequestParam(required=false) Map<String, String> parameters, Principal principal) {
		log.info("user domain " + domain + " username " + username + " principal " + principal);
		User user = users.findByDomainAndUsernameIgnoreCase(domain, username);
		if (user == null) Response.<User>builder().status(Response.Status.NOT_FOUND).build();
		log.info("user found : "+user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}
	
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public Response<User> create(@RequestBody @Valid User user) {
		users.save(user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}
	
	@RequestMapping(path = "/update", method = RequestMethod.POST)
	public Response<User> update(@RequestBody @Valid User user) {
		users.save(user);
		return Response.<User>builder().status(Response.Status.OK).data(user).build();
	}

}
