package lithium.service.user.controllers;

import lithium.service.Response;
import lithium.service.user.data.entities.UserEvent;
import lithium.service.user.data.entities.UserEventProjection;
import lithium.service.user.client.objects.UserEventBasic;
import lithium.service.user.services.UserEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/userevent")
@Slf4j
public class UserEventController {
	@Autowired UserEventService userEventService;
	
	/* should be used from authenticated systems (e.g service-casino) */
	
	@PostMapping(value="/system/{domainName}/{userName}/stream")
	public void streamUserEvent(@PathVariable String domainName, @PathVariable String userName, @RequestBody UserEventBasic userEventBasic) {
		try {
			userEventService.streamUserEvent(domainName+"/"+userName, userEventBasic.getId(), userEventBasic.getType(), userEventBasic.getMessage(), userEventBasic.getData());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@PostMapping(value="/system/{domainName}/{userName}/register")
	public Response<UserEvent> registerEvent(@PathVariable String domainName, @PathVariable String userName, @RequestBody UserEventBasic userEventBasic) throws Exception {
		try {
			UserEvent userEvent = userEventService.registerEvent(domainName+"/"+userName, userEventBasic);
			return Response.<UserEvent>builder().status(OK).data(userEvent).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserEvent>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value="/system/{domainName}/{userName}/get")
	public Response<List<UserEventProjection>> getEvents(@PathVariable String domainName, @PathVariable String userName) {
		try {
			List<UserEventProjection> userEvents = userEventService.getEvents(domainName, userName, null);
			return Response.<List<UserEventProjection>>builder().status(OK).data(userEvents).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<List<UserEventProjection>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value="/system/{domainName}/{userName}/{type}/get")
	public Response<List<UserEventProjection>> getEventsByType(@PathVariable String domainName, @PathVariable String userName, @PathVariable String type) {
		try {
			List<UserEventProjection> userEvents = userEventService.getEvents(domainName, userName, type);
			return Response.<List<UserEventProjection>>builder().status(OK).data(userEvents).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<List<UserEventProjection>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value="/system/{domainName}/{userName}/{id}/getuserevent")
	public Response<UserEvent> getUserEvent(@PathVariable String domainName, @PathVariable String userName, @PathVariable Long id) {
		try {
			UserEvent userEvent = userEventService.getUserEvent(domainName, userName, id);
			return Response.<UserEvent>builder().status(OK).data(userEvent).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserEvent>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping(value="/system/{domainName}/{userName}/{id}/received")
	public Response<UserEvent> markReceived(@PathVariable String domainName, @PathVariable String userName, @PathVariable Long id) throws Exception {
		try {
			UserEvent userEvent = userEventService.markReceived(domainName, userName, id);
			return Response.<UserEvent>builder().status(OK).data(userEvent).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserEvent>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	/* should be used from website (passing required player principal)	*/
	
	@GetMapping(value="/{domainName}/get")
	public Response<List<UserEventProjection>> getUserEvents(@PathVariable String domainName, Principal principal) {
		try {
			List<UserEventProjection> userEvents = userEventService.getEvents(domainName, principal.getName(), null);
			return Response.<List<UserEventProjection>>builder().status(OK).data(userEvents).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<List<UserEventProjection>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value="/{domainName}/{type}/get")
	public Response<List<UserEventProjection>> getUserEventsByType(@PathVariable String domainName, @PathVariable String type, Principal principal) {
		try {
			List<UserEventProjection> userEvents = userEventService.getEvents(domainName, principal.getName(), type);
			return Response.<List<UserEventProjection>>builder().status(OK).data(userEvents).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<List<UserEventProjection>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@PostMapping(value="/{domainName}/{id}/received")
	public Response<UserEvent> markUserEventReceived(@PathVariable String domainName, @PathVariable Long id, Principal principal) {
		try {
			UserEvent userEvent = userEventService.markReceived(domainName, principal.getName(), id);
			return Response.<UserEvent>builder().status(OK).data(userEvent).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserEvent>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
	
	@GetMapping(value="/{domainName}/{id}/getuserevent")
	public Response<UserEvent> getUserEvent(@PathVariable String domainName, @PathVariable Long id, Principal principal) {
		try {
			UserEvent userEvent = userEventService.getUserEvent(domainName, principal.getName(), id);
			return Response.<UserEvent>builder().status(OK).data(userEvent).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<UserEvent>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}
