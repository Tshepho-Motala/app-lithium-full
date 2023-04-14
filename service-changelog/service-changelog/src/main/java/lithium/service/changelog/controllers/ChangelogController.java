package lithium.service.changelog.controllers;

import lithium.client.changelog.objects.ChangeLog;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.changelog.services.ChangeLogService;
import lithium.service.client.page.SimplePageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/apiv1")
public class ChangelogController {
	@Autowired ChangeLogService service;
	
	@PostMapping(value = "/registerChangesWithDomain")
	public void domainInformation(@RequestBody ChangeLog changeLog) {
		service.registerChangesWithDomain(changeLog);
	}

	@RequestMapping("/list")
	public Response<ChangeLogs> list(@RequestParam String entityName, @RequestParam long entityRecordId, @RequestParam int page) {
		return Response.<ChangeLogs>builder()
			.data(service.list(entityName, entityRecordId, page))
			.status(Status.OK)
			.build();
	};
	
	@RequestMapping("/listLimited")
	public Response<ChangeLogs> listLimited(@RequestBody ChangeLogRequest changeLogRequest) {
		return Response.<ChangeLogs>builder()
			.data(service.listLimited(changeLogRequest))
			.status(Status.OK)
			.build();
	}
	
	@RequestMapping("/listLimitedPaged")
	public Response<SimplePageImpl<ChangeLog>> listLimitedPaged(@RequestBody ChangeLogRequest changeLogRequest) {
		return Response.<SimplePageImpl<ChangeLog>>builder()
			.data(service.listLimitedPaged(changeLogRequest))
			.status(Status.OK)
			.build();
	}
}
