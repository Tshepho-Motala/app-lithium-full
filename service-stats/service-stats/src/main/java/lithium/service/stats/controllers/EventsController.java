package lithium.service.stats.controllers;

import lithium.service.Response;
import lithium.service.stats.data.entities.Event;
import lithium.service.stats.data.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/events")
@RestController
@Slf4j
public class EventsController {
	@Autowired private EventRepository repository;

	@GetMapping
	public Response<Iterable<Event>> findAll() {
		return Response.<Iterable<Event>>builder().data(repository.findAll()).status(Response.Status.OK).build();
	}
}
