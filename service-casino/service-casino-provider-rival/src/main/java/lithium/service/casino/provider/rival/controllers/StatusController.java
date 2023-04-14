package lithium.service.casino.provider.rival.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;



@RestController
public class StatusController extends BaseController {
	
	@RequestMapping(params={"jsoncall=status"}, produces="application/json")
	public Response<Boolean> status() {
		return Response.<Boolean>builder().data(true).status(Status.OK).build();
	}
}
