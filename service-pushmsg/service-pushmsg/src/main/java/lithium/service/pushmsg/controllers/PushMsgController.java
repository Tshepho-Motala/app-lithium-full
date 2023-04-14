package lithium.service.pushmsg.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.pushmsg.client.objects.PushMsgBasic;
import lithium.service.pushmsg.client.stream.PushMsgStream;
import lithium.service.pushmsg.data.entities.PushMsg;
import lithium.service.pushmsg.services.PushMsgService;

@RestController
@RequestMapping("/pushmsg")
public class PushMsgController {
	@Autowired PushMsgStream pms;
	@Autowired PushMsgService pushMsgService;
	
	@GetMapping("{domainName}/table")
	public DataTableResponse<PushMsg> table(
		@PathVariable String domainName,
		DataTableRequest request,
		Principal principal
	) {
		String search = "";
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) search = request.getSearchValue();
		return new DataTableResponse<>(request, pushMsgService.findByDomain(domainName, search, request.getPageRequest()));
	}
	
	@PostMapping("/send/{domainName}")
	private void test(
		@PathVariable("domainName") String domainName,
		@RequestBody PushMsgBasic pushMsgBasic
	) {
		pms.process(
			pushMsgBasic.toBuilder()
			.domainName(domainName)
			.build()
		);
	}
	
	@GetMapping("/findOne/{id}")
	public Response<PushMsg> findOne(@PathVariable("id") Long id) throws LithiumServiceClientFactoryException {
		return Response.<PushMsg>builder().data(pushMsgService.findOne(id)).status(Status.OK).build();
	}
	
	@GetMapping("/findByDomain/table")
	private DataTableResponse<PushMsg> findByDomainTable(@RequestParam(required=false) String domainNamesCommaSeparated, @RequestParam boolean showSent, @RequestParam String createdDateStart, @RequestParam String createdDateEnd, DataTableRequest request, Principal principal) {
		Page<PushMsg> queue = pushMsgService.findByDomain(domainNamesCommaSeparated, showSent, createdDateStart, createdDateEnd, request.getSearchValue(), request.getPageRequest(), principal);
		return new DataTableResponse<>(request, queue);
	}
	
	@GetMapping("/findByUser/table")
	private DataTableResponse<PushMsg> findByUser(@RequestParam String userGuid, DataTableRequest request) {
		Page<PushMsg> userPushmsg = pushMsgService.findByUser(userGuid, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, userPushmsg);
	}
}