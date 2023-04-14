package lithium.service.user.api.frontend.controllers;

import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import lithium.service.Response;
import lithium.service.user.controllers.PubSubUserController;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserDataEntry;
import lithium.service.user.services.UserDataEntryService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/profile/data") //FIXME: should be /frontend/profile/data since this is only used by frontend
public class FrontendUserProfileDataEntryController {
	@Autowired TokenStore tokenStore;
	@Autowired UserService userService;
	@Autowired UserDataEntryService service;
	@Autowired PubSubUserController pubSubUserController;

	@GetMapping
	public Response<UserDataEntry> get(@RequestParam String key, Authentication authentication) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		User user = userService.findOne(util.id());
		if (user == null) return Response.<UserDataEntry>builder().status(NOT_FOUND).message("Invalid user").build();
		return Response.<UserDataEntry>builder().status(OK).data(	service.get(user, key)).build();
	}
	
	@PostMapping
	private Response<UserDataEntry> set(@RequestParam String key, @RequestParam String value, Authentication authentication) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		User user = userService.findOne(util.id());
		if (user == null) return Response.<UserDataEntry>builder().status(NOT_FOUND).message("Invalid user").build();
		UserDataEntry data = service.set(user, key, value);
		pubSubUserController.pushToPubSub(user.guid(),authentication);
		return Response.<UserDataEntry>builder().status(OK).data(data).build();
	}
	
}
