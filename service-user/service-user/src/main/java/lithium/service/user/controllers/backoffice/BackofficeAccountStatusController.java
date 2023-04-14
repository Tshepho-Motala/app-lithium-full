package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backoffice/account-status/{domainName}")
public class BackofficeAccountStatusController {
	@Autowired private StatusService service;

	@GetMapping("/{statusId}/find-reasons")
	public Response<List<StatusReason>> findReasonsByStatus(@PathVariable("statusId") Status status) {
		return Response.<List<StatusReason>>builder().data(service.findReasonsByStatus(status))
			.status(Response.Status.OK).build();
	}
}
