package lithium.service.xp.controllers.admin;

import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.xp.data.entities.Status;
import lithium.service.xp.services.StatusService;

@RestController
@RequestMapping("/admin/status")
public class StatusAdminController {
	@Autowired StatusService service;
	
	@GetMapping("/all")
	public Response<Iterable<Status>> all() {
		return Response.<Iterable<Status>>builder().data(service.findAll()).status(OK).build();
	}
}
