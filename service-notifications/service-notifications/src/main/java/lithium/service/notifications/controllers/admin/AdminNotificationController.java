package lithium.service.notifications.controllers.admin;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.notifications.data.entities.Notification;
import lithium.service.notifications.data.entities.NotificationChannel;
import lithium.service.notifications.services.NotificationProcessor;
import lithium.service.notifications.services.NotificationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/notification")
@Slf4j
public class AdminNotificationController {
	@Autowired NotificationService service;
	@Autowired NotificationProcessor notificationProcessor;
//	@Autowired NotificationStream notificationStream;

	@PostMapping("/send")
	public Response<Boolean> send(
		@RequestParam(name="userGuid", required=true) String userGuid,
		@RequestParam(name="notificationName", required=true) String notificationName
	) {
		try {
			// TODO: add placeholder replacements for notifications sent via admin
			notificationProcessor.process(userGuid, notificationName, null);
//			notificationStream.process(
//				UserNotification.builder()
//				.userGuid(userGuid)
//				.notificationName(notificationName)
//				.build()
//			);
			return Response.<Boolean>builder().data(true).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Boolean>builder().data(false).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/findByDomainName")
	public Response<List<Notification>> findByDomainName(@RequestParam("domainName") String domainName) {
		return Response.<List<Notification>>builder().data(service.findByDomainName(domainName)).status(OK).build();
	}
	
	@GetMapping("/findByDomainNameAndName")
	public Response<Notification> findByDomainNameAndName(@RequestParam("domainName") String domainName, @RequestParam("name") String name) {
		Notification notification = null;
		try {
			notification = service.findByDomainNameAndName(domainName, name);
			if (notification == null) {
				return Response.<Notification>builder().data(notification).status(NOT_FOUND).build();
			} else {
				return Response.<Notification>builder().data(notification).status(OK).build();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/table")
	public DataTableResponse<Notification> table(@RequestParam("domains") List<String> domains, DataTableRequest request) {
		Page<Notification> table = service.findByDomains(domains,
				request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/{id}")
	public Response<Notification> get(@PathVariable("id") Notification notification) {
		return Response.<Notification>builder().data(notification).status(OK).build();
	}
	
	@PostMapping("/create")
	public Response<Notification> create(@RequestBody lithium.service.notifications.client.objects.Notification notificationPost) {
		Notification notification = null;
		try {
			notification = service.create(notificationPost);
			return Response.<Notification>builder().data(notification).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/{id}/modify")
	public Response<Notification> modify(
		@PathVariable("id") Notification notification,
		@RequestBody lithium.service.notifications.client.objects.Notification notificationPost
	) {
		try {
			notification = service.modify(notification, notificationPost);
			return Response.<Notification>builder().data(notification).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/{id}/addChannel")
	public Response<Notification> addChannel(
		@PathVariable("id") Notification notification,
		@RequestBody lithium.service.notifications.client.objects.NotificationChannel notificationChannelPost
	) {
		try {
			notification = service.addChannel(notification, notificationChannelPost);
			return Response.<Notification>builder().data(notification).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@DeleteMapping("/{id}/removeChannel/{notificationChannelId}")
	public Response<Notification> removeChannel(
		@PathVariable("id") Notification notification,
		@PathVariable("notificationChannelId") NotificationChannel notificationChannel
	) {
		try {
			notification = service.removeChannel(notification, notificationChannel);
			return Response.<Notification>builder().data(notification).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/{id}/modifyChannel/{notificationChannelId}")
	public Response<Notification> modifyChannel(
		@PathVariable("id") Notification notification,
		@PathVariable("notificationChannelId") NotificationChannel notificationChannel,
		@RequestBody lithium.service.notifications.client.objects.NotificationChannel notificationChannelPost
	) {
		try {
			notification = service.modifyChannel(notification, notificationChannel, notificationChannelPost);
			return Response.<Notification>builder().data(notification).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Notification>builder().data(notification).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
