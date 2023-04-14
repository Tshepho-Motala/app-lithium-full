package lithium.service.user.controllers;

import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.user.data.entities.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
	@Autowired private StatusService service;

	@GetMapping("/find-all-reasons")
	public Response<Iterable<StatusReason>> findAllReasons() {
		return Response.<Iterable<StatusReason>>builder().data(service.findAllStatusReasons())
			.status(Response.Status.OK).build();
	}
	
	@GetMapping("/all")
	public Response<Iterable<Status>> all() throws Exception {
		log.trace("");
		return Response.<Iterable<Status>>builder().data(service.findAllNotDeletedStatuses()).build();
	}
}
