package lithium.service.user.client;

import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.IncompleteUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-user")
public interface IncompleteUserClient {
	@RequestMapping(path = "/{domainName}/incompleteusersforreport/table")
	public DataTableResponse<IncompleteUser> tableForIncompleteUserReport(
		@PathVariable("domainName") String domainName,
		@RequestParam("matchAllFilters") Boolean matchAllFilters,
		@RequestParam("filters") String filters,
		@RequestParam("draw") String drawEcho,
		@RequestParam("start") Long start,
		@RequestParam("length") Long length);
}
