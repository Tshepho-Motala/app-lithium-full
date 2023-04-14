package lithium.service.notifications.controllers.admin;

import static lithium.service.Response.Status.OK;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.notifications.data.entities.Inbox;
import lithium.service.notifications.services.InboxService;

@RestController
@RequestMapping("/admin/inbox")
public class AdminInboxController {
	@Autowired InboxService service;
	
	@GetMapping("/table")
	public DataTableResponse<Inbox> table(
		@RequestParam("domains") List<String> domains,
		@RequestParam("showRead") Boolean showRead,
		@RequestParam("showUnread") Boolean showUnread,
		@RequestParam(name="userGuid",required=false) String userGuid,
		DataTableRequest request
	) {
		if (!showRead && !showUnread) {
			return new DataTableResponse<>(request, new ArrayList<Inbox>());
		}
		Page<Inbox> table = service.findByDomains(domains, showRead, showUnread, userGuid,
				request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
	
	@GetMapping("/{id}")
	public Response<Inbox> get(@PathVariable("id") Long id) {
		return Response.<Inbox>builder().data(service.findById(id)).status(OK).build();
	}
}
