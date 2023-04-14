package lithium.service.casino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.casino.service.CasinoService;
import lithium.service.user.client.objects.UserApiToken;

@RestController
public class AuthenticateController {
	@Autowired
	private CasinoService casinoService;

	@RequestMapping("/casino/authenticate")
	public Response<Boolean> authenticate(@RequestParam("guid") String guid, @RequestParam("userApiToken") String userApiToken) throws Exception {
		UserApiToken token = casinoService.validateUserSession(guid, userApiToken);
		
		if(token != null) {
			return Response.<Boolean>builder().data(true).status(Status.OK).build();
		}
		
		return Response.<Boolean>builder().data(false).status(Status.INVALID_DATA).build();
	}
}
