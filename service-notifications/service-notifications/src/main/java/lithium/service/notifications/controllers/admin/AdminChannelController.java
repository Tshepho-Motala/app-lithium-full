package lithium.service.notifications.controllers.admin;

import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.notifications.data.entities.Channel;
import lithium.service.notifications.data.repositories.ChannelRepository;

@RestController
@RequestMapping("/admin/channel")
public class AdminChannelController {
	@Autowired ChannelRepository repository;
	
	@GetMapping("/all")
	public Response<Iterable<Channel>> all() {
		return Response.<Iterable<Channel>>builder().data(repository.findAll()).status(OK).build();
	}
}
