package lithium.service.pushmsg.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.pushmsg.data.entities.User;
import lithium.service.pushmsg.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pushmsg/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/{domainName}/list")
	private Response<List<User>> find(
		@PathVariable("domainName") String domainName,
		@RequestParam(name="search", required=false) String search
	) {
		return Response.<List<User>>builder().data(userService.find(domainName, search)).status(Status.OK).build();
	}
	
	@GetMapping("/{domainName}")
	private Response<List<User>> find(
		@PathVariable("domainName") String domainName
	) {
		return Response.<List<User>>builder().data(userService.find(domainName)).status(Status.OK).build();
	}
	
	@PostMapping("/multi")
	private Response<User> findAndUpdateMultiple(
		@RequestParam(name="guid", required=true) String guid,
		@RequestParam(name="uuids", required=true) List<String> externalPlayerIds
	) {
		return Response.<User>builder().data(userService.findAndUpdate(guid, externalPlayerIds)).status(Status.OK).build();
	}
	
	@PostMapping("/single")
	private Response<User> findAndUpdateSingle(
		@RequestParam(name="guid", required=true) String guid,
		@RequestParam(name="uuid", required=true) String externalPlayerId
	) {
		log.info("findAndUpdateSingle("+guid+", "+externalPlayerId+")");
		return Response.<User>builder().data(userService.findAndUpdate(guid, externalPlayerId)).status(Status.OK).build();
	}
}