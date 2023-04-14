package lithium.service.access.client;

import lithium.service.access.client.objects.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient("service-access")
public interface ListClient {
	@RequestMapping("/lists/findByName/{domainName}")
	public Response<List> findByDomainNameAndName(@PathVariable("domainName") String domainName, @RequestParam("listName") String listName);

	@RequestMapping(value = "/system/list/{id}/add-data-value", method=RequestMethod.POST)
	public Response<List> addListValue(@PathVariable("id") Long listId, @RequestBody String data) throws Exception;

	@RequestMapping(value = "/system/list/{id}/remove-data-value", method=RequestMethod.POST)
	public Response<List> removeListDataValue(@PathVariable("id") Long listId, @RequestBody String data) throws Exception;
}