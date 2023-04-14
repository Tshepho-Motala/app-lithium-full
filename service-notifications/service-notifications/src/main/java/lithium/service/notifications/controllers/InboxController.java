package lithium.service.notifications.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.services.InboxService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/inbox/{domainName}/{userName}")
@Slf4j
public class InboxController {
	@Autowired InboxService service;
	
	@GetMapping("/table/all")
	public DataTableResponse<Inbox> getInbox(
		@PathVariable("domainName") String domainName,
		@PathVariable("userName") String userName,
		DataTableRequest request
	) {
		List<String> domains = new ArrayList<>();
		domains.add(domainName);
		Page<Inbox> table = service.findByDomains(domains, true, true, domainName+"/"+userName,
				request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/table/read")
	public DataTableResponse<Inbox> getReadInbox(
		@PathVariable("domainName") String domainName,
		@PathVariable("userName") String userName,
		DataTableRequest request
	) {
		List<String> domains = new ArrayList<>();
		domains.add(domainName);
		Page<Inbox> table = service.findByDomains(domains, true, false, domainName+"/"+userName,
				request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/table/unread")
	public DataTableResponse<Inbox> getUnreadInbox(
		@PathVariable("domainName") String domainName,
		@PathVariable("userName") String userName,
		DataTableRequest request
	) {
		List<String> domains = new ArrayList<>();
		domains.add(domainName);
		Page<Inbox> table = service.findByDomains(domains, false, true, domainName+"/"+userName,
				request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@PostMapping("/{id}/markRead")
	public Response<Inbox> markRead(@PathVariable("id") Long id) {
		Inbox inbox = null;
		try {
			inbox = service.markRead(id);
			return Response.<Inbox>builder().data(inbox).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Inbox>builder().data(inbox).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
