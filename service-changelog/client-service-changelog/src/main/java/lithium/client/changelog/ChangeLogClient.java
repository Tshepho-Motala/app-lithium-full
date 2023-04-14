package lithium.client.changelog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.client.changelog.objects.ChangeLog;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.page.SimplePageImpl;

@FeignClient(name="service-changelog")
public interface ChangeLogClient {
	@RequestMapping(method = RequestMethod.POST, path="/apiv1/registerChangesWithDomain")
	public void registerChangesWithDomain(@RequestBody ChangeLog changeLog);

	@RequestMapping(method = RequestMethod.GET, path="/apiv1/list")
	public Response<ChangeLogs> list(@RequestParam(name="entityName") String entity, @RequestParam(name="entityRecordId") long entityRecordId, @RequestParam(name="page") int page);
	
	@RequestMapping(method = RequestMethod.GET, path="/apiv1/listLimited")
	public Response<ChangeLogs> listLimited(@RequestBody ChangeLogRequest changeLogRequest);
	
	@RequestMapping(method = RequestMethod.GET, path="/apiv1/listLimitedPaged")
	public Response<SimplePageImpl<ChangeLog>> listLimitedPaged(@RequestBody ChangeLogRequest changeLogRequest);

	@RequestMapping(method = RequestMethod.POST, path = "/system/changelogs/user/add-note")
	public Response<String> addNote(@RequestBody lithium.client.changelog.objects.ChangeLog changeLog) throws Exception;
	@RequestMapping(value = "/system/changelogs/add-domain", method = RequestMethod.GET)
	void addDomain(@RequestParam("domainName") String domainName);

}
