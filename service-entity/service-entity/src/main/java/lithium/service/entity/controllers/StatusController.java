package lithium.service.entity.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.entity.data.entities.Status;
import lithium.service.entity.data.repositories.StatusRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
	@Autowired
	private StatusRepository statusRepository;
	
	@GetMapping("/all")
	public Response<Iterable<Status>> all() throws Exception {
		log.trace("");
		return Response.<Iterable<Status>>builder().data(statusRepository.findAll()).build();
	}
}